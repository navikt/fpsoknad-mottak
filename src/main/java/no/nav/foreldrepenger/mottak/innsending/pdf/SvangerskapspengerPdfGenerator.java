package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000049;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000060;
import static no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper.SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.Tilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Frilanser;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@Service
public class SvangerskapspengerPdfGenerator implements MappablePdfGenerator {
    private static final String SVP_VEDLEGG_TILRETTELEGGING = "svp.vedlegg.tilrettelegging";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DATEFMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final float STARTY = PdfElementRenderer.calculateStartY();
    private static final int INDENT = 20;
    private final PdfElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;
    private final SvangerskapspengerInfoRenderer infoRenderer;
    private final ArbeidsInfo arbeidsforhold;

    @Inject
    public SvangerskapspengerPdfGenerator(PdfElementRenderer renderer,
            SøknadTextFormatter textFormatter,
            ArbeidsInfo arbeidsforhold, SvangerskapspengerInfoRenderer infoRenderer) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
        this.arbeidsforhold = arbeidsforhold;
        this.infoRenderer = infoRenderer;
    }

    @Override
    public byte[] generer(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        var svp = Svangerskapspenger.class.cast(søknad.getYtelse());
        var arbeidsforhold = aktiveArbeidsforhold(svp.getTermindato(), svp.getFødselsdato());
        try (var doc = new FontAwarePdfDocument(); var baos = new ByteArrayOutputStream()) {
            var page = newPage();
            doc.addPage(page);
            var cos = new FontAwareCos(doc, page);
            float y = STARTY;
            y -= header(søker, doc, cos, y);
            float headerSize = STARTY - y;
            y -= omBarn(svp, cos, y);
            y -= blankLine();
            var opptjening = svp.getOpptjening();
            if (!svp.getTilrettelegging().isEmpty()) {
                y -= renderer.addLeftHeading(textFormatter.fromMessageSource("tilrettelegging"), cos, y);
                var tilretteleggingsPerioder = tilretteleggingByArbeidsforhold(
                        svp.getTilrettelegging());
                // type arbeidsforhold kommer i random rekkefølge
                for (var arb : tilretteleggingsPerioder.entrySet()) {
                    var tilrettelagtArbeidsforhold = arb.getKey();
                    var tilrettelegging = sortertTilretteleggingsliste(arb.getValue());
                    var scratch1 = newPage();
                    var scratchcos = new FontAwareCos(doc, scratch1);
                    float startY = STARTY;
                    startY -= header(søker, doc, scratchcos, startY);
                    float size = renderTilrettelegging(arbeidsforhold, tilrettelagtArbeidsforhold, tilrettelegging,
                            søknad.getVedlegg(), scratchcos,
                            startY);
                    float behov = startY - size;
                    if (behov < y) {
                        scratchcos.close();
                        y = renderTilrettelegging(arbeidsforhold, tilrettelagtArbeidsforhold, tilrettelegging,
                                søknad.getVedlegg(),
                                cos, y);
                    } else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }
            }
            if (!arbeidsforhold.isEmpty()) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY -= header(søker, doc, scratchcos, startY);
                float size = infoRenderer.arbeidsforholdOpptjening(arbeidsforhold, scratchcos, startY);
                float behov = startY - size;
                if (behov < y) {
                    scratchcos.close();
                    y = infoRenderer.arbeidsforholdOpptjening(arbeidsforhold, cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }
            if (opptjening.getFrilans() != null) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY -= header(søker, doc, scratchcos, startY);
                float size = infoRenderer.frilansOpptjening(svp.getOpptjening().getFrilans(), scratchcos, startY);
                float behov = startY - size;
                if (behov < y) {
                    scratchcos.close();
                    y = infoRenderer.frilansOpptjening(svp.getOpptjening().getFrilans(), cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }
            if (!opptjening.getEgenNæring().isEmpty()) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY -= header(søker, doc, scratchcos, startY);
                float size = infoRenderer.egneNæringerOpptjening(opptjening.getEgenNæring(), scratchcos, startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = infoRenderer.egneNæringerOpptjening(opptjening.getEgenNæring(), cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }
            if (!opptjening.getUtenlandskArbeidsforhold().isEmpty()) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY -= header(søker, doc, scratchcos, startY);
                float size = infoRenderer.utenlandskeArbeidsforholdOpptjening(
                        opptjening.getUtenlandskArbeidsforhold(),
                        søknad.getVedlegg(),
                        scratchcos, startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = infoRenderer.utenlandskeArbeidsforholdOpptjening(
                            opptjening.getUtenlandskArbeidsforhold(),
                            søknad.getVedlegg(), cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }
            if (svp.getMedlemsskap() != null) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY -= header(søker, doc, scratchcos, startY);
                float size = renderMedlemskap(svp.getMedlemsskap(), scratchcos, startY);
                float behov = startY - size;
                if (behov < y) {
                    scratchcos.close();
                    y = renderMedlemskap(svp.getMedlemsskap(), cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }
            cos.close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UnexpectedInputException("Kunne ikke lage PDF", e);
        }
    }

    private List<EnkeltArbeidsforhold> aktiveArbeidsforhold(LocalDate termindato, LocalDate fødselsdato) {
        var relasjonsDato = fødselsdato != null ? fødselsdato : termindato;
        return safeStream(arbeidsforhold.hentAktiveArbeidsforhold())
            .filter(a -> a.getTo().isEmpty() || (a.getTo().isPresent() && a.getTo().get().isAfter(relasjonsDato)))
            .collect(Collectors.toList());
    }

    private float renderTilrettelegging(List<EnkeltArbeidsforhold> arbeidsgivere,
            Arbeidsforhold arbeidsforhold,
            List<Tilrettelegging> tilrettelegging,
            List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        if (arbeidsforhold instanceof Virksomhet v) {
            y -= renderer.addLineOfRegularText(
                    virksomhetsnavn(arbeidsgivere, v.getOrgnr()), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof PrivatArbeidsgiver) {
            y -= renderer.addLineOfRegularText(txt("svp.privatarbeidsgiver"), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof SelvstendigNæringsdrivende s) {
            y -= renderer.addLineOfRegularText(txt("svp.selvstendig"), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer",
                    s.getRisikoFaktorer()),
                    cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak",
                    s.getTilretteleggingstiltak()),
                    cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof Frilanser f) {
            y -= renderer.addLineOfRegularText(txt("svp.frilans"), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer",
                    f.getRisikoFaktorer()), cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak",
                    f.getTilretteleggingstiltak()), cos, y);
            y -= blankLine();
        }
        return y;
    }

    private static Map<Arbeidsforhold, List<Tilrettelegging>> tilretteleggingByArbeidsforhold(
            List<Tilrettelegging> tilretteleggingsPerioder) {
        Map<Arbeidsforhold, List<Tilrettelegging>> tilretteleggingByArbeidsforhold = new HashMap<>();
        tilretteleggingsPerioder.forEach(tp -> tilretteleggingByArbeidsforhold
                .computeIfAbsent(tp.getArbeidsforhold(), key -> new ArrayList<>())
                .add(tp));
        return tilretteleggingByArbeidsforhold;
    }

    private float renderMedlemskap(Medlemsskap medlemsskap, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("medlemsskap"), cos, y);
        var tidligereOpphold = medlemsskap.getTidligereOppholdsInfo();
        var framtidigeOpphold = medlemsskap.getFramtidigOppholdsInfo();
        y -= renderer.addLineOfRegularText(txt("siste12") +
                (tidligereOpphold.isBoddINorge() ? " Norge" : ":"), cos, y);
        if (!tidligereOpphold.getUtenlandsOpphold().isEmpty()) {
            y -= renderer.addBulletList(textFormatter.utenlandsOpphold(tidligereOpphold.getUtenlandsOpphold()),
                    cos, y);
        }
        y -= renderer.addLineOfRegularText(txt("neste12") +
                (framtidigeOpphold.isNorgeNeste12() ? " Norge" : ":"), cos, y);
        if (!framtidigeOpphold.getUtenlandsOpphold().isEmpty()) {
            y -= renderer.addBulletList(textFormatter.utenlandsOpphold(framtidigeOpphold.getUtenlandsOpphold()),
                    cos,
                    y);
        }
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private static List<Tilrettelegging> sortertTilretteleggingsliste(List<Tilrettelegging> liste) {
        return safeStream(liste)
                .sorted(Comparator.comparing(Tilrettelegging::getBehovForTilretteleggingFom))
                .collect(Collectors.toList());
    }

    private float renderTilretteleggingsperioder(List<Tilrettelegging> perioder,
            List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        var tilrettelegging = perioder.stream().findAny().orElseThrow(IllegalArgumentException::new);
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.behovfra", DATEFMT.format(tilrettelegging.getBehovForTilretteleggingFom())), cos, y);
        for (var periode : perioder) {
            if (periode instanceof HelTilrettelegging h) {
                y -= renderHelTilrettelegging(h, cos, y);
            } else if (periode instanceof DelvisTilrettelegging d) {
                y -= renderDelvisTilrettelegging(d, cos, y);
            } else if (periode instanceof IngenTilrettelegging i) {
                y -= renderIngenTilrettelegging(i, cos, y);
            }
        }
        var vedleggRefs = perioder.stream()
                .map(Tilrettelegging::getVedlegg)
                .findAny()
                .orElseGet(List::of);

        y -= renderVedlegg(vedlegg, vedleggRefs, SVP_VEDLEGG_TILRETTELEGGING, cos, y);
        return startY - y;
    }

    private float renderIngenTilrettelegging(IngenTilrettelegging periode, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.sluttearbeid", DATEFMT.format(periode.getSlutteArbeidFom())), cos, y);
        return startY - y;
    }

    private float renderDelvisTilrettelegging(DelvisTilrettelegging periode, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.tilretteleggingfra", DATEFMT.format(periode.getTilrettelagtArbeidFom())), cos, y);
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.stillingsprosent", prosentFra(periode.getStillingsprosent())), cos, y);
        return startY - y;
    }

    private static double prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::getProsent)
                .orElse(0d);
    }

    private float renderHelTilrettelegging(HelTilrettelegging periode, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT, txt("svp.tilretteleggingfra",
                DATEFMT.format(periode.getTilrettelagtArbeidFom())), cos, y);
        return startY - y;
    }

    private float renderVedlegg(List<Vedlegg> vedlegg, List<String> vedleggRefs, String keyIfAnnet,
            FontAwareCos cos, float y) throws IOException {
        float startY = y;
        if (!vedleggRefs.isEmpty()) {
            y -= renderer.addBulletPoint(INDENT, txt("vedlegg1"), cos, y);
        }
        for (var vedleggRef : vedleggRefs) {
            var details = safeStream(vedlegg)
                    .filter(s -> vedleggRef.equals(s.getId()))
                    .findFirst();
            if (details.isPresent()) {
                String beskrivelse = vedleggsBeskrivelse(keyIfAnnet, details.get());
                y -= renderer.addBulletPoint(INDENT * 2,
                        txt("vedlegg2", beskrivelse, details.get().getInnsendingsType().name()),
                        cos, y);
            } else {
                // Never, hopefully
                y -= renderer.addBulletPoint(INDENT * 2, txt("vedlegg2", "vedlegg"), cos, y);
            }
        }
        return startY - y;
    }

    private String vedleggsBeskrivelse(String key, Vedlegg vedlegg) {
        return erAnnenDokumentType(vedlegg) ? txt(key) : vedlegg.getBeskrivelse();
    }

    private static boolean erAnnenDokumentType(Vedlegg vedlegg) {
        return vedlegg.getDokumentType().equals(I000060) || vedlegg.getDokumentType().equals(I000049);
    }

    private String virksomhetsnavn(List<EnkeltArbeidsforhold> arbeidsgivere, Orgnummer orgnr) {
        return safeStream(arbeidsgivere)
                .filter(arb -> arb.getArbeidsgiverId().equals(orgnr.value()))
                .findFirst()
                .map(EnkeltArbeidsforhold::getArbeidsgiverNavn)
                .orElse(txt("arbeidsgiverIkkeFunnet", orgnr.value()));
    }

    private float omBarn(Svangerskapspenger svp, FontAwareCos cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("ombarn"), cos, y);
        if (svp.getFødselsdato() != null) {
            y -= renderer.addLineOfRegularText(
                    txt("fødselsdato", DATEFMT.format(svp.getFødselsdato())), cos, y);
            y -= renderer.addLineOfRegularText(
                    txt("fødselmedtermin", DATEFMT.format(svp.getTermindato())), cos, y);
        } else {
            y -= renderer.addLineOfRegularText(
                    txt("svp.termindato", DATEFMT.format(svp.getTermindato())), cos, y);
        }
        return startY - y;
    }

    private float header(Person søker, FontAwarePdfDocument doc, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addLogo(doc, cos, y);
        y -= renderer.addCenteredHeading(textFormatter.fromMessageSource("svp.søknad"), cos, y);
        y -= renderer.addCenteredRegular(
                textFormatter.fromMessageSource("mottatt", FMT.format(LocalDateTime.now())), cos, y);
        y -= renderer.addCenteredRegulars(søker(søker), cos, y);
        y -= renderer.addDividerLine(cos, y);
        return startY - y;
    }

    private List<String> søker(Person søker) {
        return Arrays.asList(
                textFormatter.navn(
                        new Navn(søker.getFornavn(), søker.getMellomnavn(), søker.getEtternavn(), søker.getKjønn())),
                textFormatter.fromMessageSource("fødselsnummerinline", søker.getFnr().getFnr()));
    }

    private String txt(String key, Object... values) {
        return textFormatter.fromMessageSource(key, values);
    }

    private static float blankLine() {
        return PdfElementRenderer.BLANK_LINE;
    }

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    private static float nesteSideStart(float headerSize, float behov) {
        return STARTY - behov - headerSize;
    }

    private static FontAwareCos nySide(FontAwarePdfDocument doc, FontAwareCos cos, PDPage scratch,
            FontAwareCos scratchcos)
            throws IOException {
        cos.close();
        doc.addPage(scratch);
        return scratchcos;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return SVANGERSKAPSPENGER;
    }
}
