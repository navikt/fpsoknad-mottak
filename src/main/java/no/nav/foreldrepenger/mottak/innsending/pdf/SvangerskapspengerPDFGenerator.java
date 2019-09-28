package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000049;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000060;
import static no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper.SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Frilanser;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@Service
public class SvangerskapspengerPDFGenerator implements PDFGenerator {
    private static final String SVP_VEDLEGG_TILRETTELEGGING = "svp.vedlegg.tilrettelegging";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DATEFMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final float STARTY = PDFElementRenderer.calculateStartY();
    private static final int INDENT = 20;
    private final PDFElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;
    private final SvangerskapspengerInfoRenderer infoRenderer;
    private final Oppslag oppslag;

    @Inject
    public SvangerskapspengerPDFGenerator(PDFElementRenderer renderer,
            SøknadTextFormatter textFormatter,
            Oppslag oppslag, SvangerskapspengerInfoRenderer infoRenderer) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
        this.oppslag = oppslag;
        this.infoRenderer = infoRenderer;
    }

    @Override
    public byte[] generer(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        var svp = (Svangerskapspenger) søknad.getYtelse();
        List<no.nav.foreldrepenger.mottak.domain.Arbeidsforhold> arbeidsforhold = oppslag.getArbeidsforhold();
        try (var doc = new FontAwarePDDocument(); var baos = new ByteArrayOutputStream()) {
            var page = newPage();
            doc.addPage(page);
            var cos = new FontAwareCos(doc, page);
            float y = STARTY;
            y -= header(søker, doc, cos, y);
            float headerSize = STARTY - y;
            y -= omBarn(svp, cos, y);
            y -= blankLine();
            Opptjening opptjening = svp.getOpptjening();
            if (!svp.getTilrettelegging().isEmpty()) {
                y -= renderer.addLeftHeading(textFormatter.fromMessageSource("tilrettelegging"), cos, y);
                var tilretteleggingsPerioder = tilretteleggingByArbeidsforhold(
                        svp.getTilrettelegging());
                // type arbeidsforhold kommer i random rekkefølge
                for (var arb : tilretteleggingsPerioder.entrySet()) {
                    var tilrettelagtArbeidsforhold = arb.getKey();
                    List<Tilrettelegging> tilrettelegging = sortertTilretteleggingsliste(arb.getValue());
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

    private float renderTilrettelegging(List<no.nav.foreldrepenger.mottak.domain.Arbeidsforhold> arbeidsgivere,
            Arbeidsforhold arbeidsforhold, List<Tilrettelegging> tilrettelegging,
            List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        if (arbeidsforhold instanceof Virksomhet) {
            y -= renderer.addLineOfRegularText(
                    virksomhetsnavn(arbeidsgivere, Virksomhet.class.cast(arbeidsforhold).getOrgnr()), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof PrivatArbeidsgiver) {
            y -= renderer.addLineOfRegularText(txt("svp.privatarbeidsgiver"), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof SelvstendigNæringsdrivende) {
            y -= renderer.addLineOfRegularText(txt("svp.selvstendig"), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer",
                    SelvstendigNæringsdrivende.class.cast(arbeidsforhold).getRisikoFaktorer()),
                    cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak",
                    SelvstendigNæringsdrivende.class.cast(arbeidsforhold).getTilretteleggingstiltak()),
                    cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof Frilanser) {
            y -= renderer.addLineOfRegularText(txt("svp.frilans"), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer",
                    Frilanser.class.cast(arbeidsforhold).getRisikoFaktorer()), cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak",
                    Frilanser.class.cast(arbeidsforhold).getTilretteleggingstiltak()), cos, y);
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
        y -= PDFElementRenderer.BLANK_LINE;
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
        for (Tilrettelegging periode : perioder) {
            y -= renderer.addBulletPoint(INDENT,
                    txt("svp.behovfra", DATEFMT.format(periode.getBehovForTilretteleggingFom())), cos, y);
            if (periode instanceof HelTilrettelegging) {
                y -= renderHelTilrettelegging(HelTilrettelegging.class.cast(periode), vedlegg, cos, y);
            } else if (periode instanceof DelvisTilrettelegging) {
                y -= renderDelvisTilrettelegging(DelvisTilrettelegging.class.cast(periode), vedlegg, cos, y);
            } else if (periode instanceof IngenTilrettelegging) {
                y -= renderIngenTilrettelegging(IngenTilrettelegging.class.cast(periode), vedlegg, cos, y);
            }
        }
        return startY - y;
    }

    private float renderIngenTilrettelegging(IngenTilrettelegging periode, List<Vedlegg> vedlegg, FontAwareCos cos,
            float y)
            throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.sluttearbeid", DATEFMT.format(periode.getSlutteArbeidFom())), cos, y);
        y -= renderVedlegg(vedlegg, periode.getVedlegg(), SVP_VEDLEGG_TILRETTELEGGING, cos, y);
        return startY - y;
    }

    private float renderDelvisTilrettelegging(DelvisTilrettelegging periode,
            List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.tilretteleggingfra", DATEFMT.format(periode.getTilrettelagtArbeidFom())), cos, y);
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.stillingsprosent", prosentFra(periode.getStillingsprosent())), cos, y);
        y -= renderVedlegg(vedlegg, periode.getVedlegg(), SVP_VEDLEGG_TILRETTELEGGING, cos, y);
        return startY - y;
    }

    private static double prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::getProsent)
                .orElse(0d);
    }

    private float renderHelTilrettelegging(HelTilrettelegging periode,
            List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT, txt("svp.tilretteleggingfra",
                DATEFMT.format(periode.getTilrettelagtArbeidFom())), cos, y);
        y -= renderVedlegg(vedlegg, periode.getVedlegg(), SVP_VEDLEGG_TILRETTELEGGING, cos, y);
        return startY - y;
    }

    private float renderVedlegg(List<Vedlegg> vedlegg, List<String> vedleggRefs, String keyIfAnnet,
            FontAwareCos cos, float y) throws IOException {
        float startY = y;
        if (!vedleggRefs.isEmpty()) {
            y -= renderer.addBulletPoint(INDENT, txt("vedlegg1"), cos, y);
        }
        for (String vedleggRef : vedleggRefs) {
            Optional<Vedlegg> details = safeStream(vedlegg)
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

    private String virksomhetsnavn(List<no.nav.foreldrepenger.mottak.domain.Arbeidsforhold> arbeidsgivere,
            String orgnr) {
        return safeStream(arbeidsgivere)
                .filter(arb -> arb.getArbeidsgiverId().equals(orgnr))
                .findFirst()
                .map(no.nav.foreldrepenger.mottak.domain.Arbeidsforhold::getArbeidsgiverNavn)
                .orElse(txt("arbeidsgiverIkkeFunnet", orgnr));
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

    private float header(Person søker, FontAwarePDDocument doc, FontAwareCos cos, float y)
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
                textFormatter.fromMessageSource("fødselsnummer", søker.getFnr().getFnr()));
    }

    private String txt(String key, Object... values) {
        return textFormatter.fromMessageSource(key, values);
    }

    private static float blankLine() {
        return PDFElementRenderer.BLANK_LINE;
    }

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    private static float nesteSideStart(float headerSize, float behov) {
        return STARTY - behov - headerSize;
    }

    private static FontAwareCos nySide(FontAwarePDDocument doc, FontAwareCos cos, PDPage scratch,
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
