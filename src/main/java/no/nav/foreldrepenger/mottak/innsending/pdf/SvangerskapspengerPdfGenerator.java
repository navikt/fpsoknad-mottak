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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.VedleggReferanse;
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
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@Service
public class SvangerskapspengerPdfGenerator implements MappablePdfGenerator {
    private static final String SVP_VEDLEGG_TILRETTELEGGING = "svp.vedlegg.tilrettelegging";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DATEFMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final float STARTY = PdfElementRenderer.calculateStartY();
    private static final int INDENT = 20;
    private static final int DOUBLE_INDENT = INDENT * 2;
    private final PdfElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;
    private final SvangerskapspengerInfoRenderer infoRenderer;
    private final ArbeidsInfo arbeidsInfo;

    @Autowired
    public SvangerskapspengerPdfGenerator(PdfElementRenderer renderer,
                                          SøknadTextFormatter textFormatter,
                                          ArbeidsInfo arbeidsInfo,
                                          SvangerskapspengerInfoRenderer infoRenderer) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
        this.arbeidsInfo = arbeidsInfo;
        this.infoRenderer = infoRenderer;
    }

    @Override
    public byte[] generer(Søknad søknad, SøknadEgenskap egenskap, InnsendingPersonInfo person) {
        var svp = (Svangerskapspenger) søknad.getYtelse();
        var arbeidsforhold = aktiveArbeidsforhold(svp.getTidligstDatoForTilrettelegging());
        try (var doc = new FontAwarePdfDocument(); var baos = new ByteArrayOutputStream()) {
            var page = newPage();
            doc.addPage(page);
            var cos = new FontAwareCos(doc, page);
            var y = STARTY;
            y -= header(doc, cos, y, person);
            var headerSize = STARTY - y;
            y -= omBarn(svp, cos, y);
            y -= blankLine();
            var opptjening = svp.opptjening();
            if (!svp.tilrettelegging().isEmpty()) {
                y -= renderer.addLeftHeading(textFormatter.fromMessageSource("tilrettelegging"), cos, y);
                var arbeidsforholdTilretteleggingMap = svp.tilrettelegging().stream()
                    .collect(Collectors.groupingBy(Tilrettelegging::getArbeidsforhold));
                // type arbeidsforhold kommer i random rekkefølge
                for (var arb : arbeidsforholdTilretteleggingMap.entrySet()) {
                    var tilrettelagtArbeidsforhold = arb.getKey();
                    var tilrettelegging = sortertTilretteleggingsliste(arb.getValue());
                    var scratch1 = newPage();
                    var scratchcos = new FontAwareCos(doc, scratch1);
                    var startY = STARTY;
                    startY -= header(doc, scratchcos, startY, person);
                    var size = renderTilrettelegging(arbeidsforhold, tilrettelagtArbeidsforhold, tilrettelegging,
                            søknad.getVedlegg(), scratchcos,
                            startY);
                    var behov = startY - size;
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
                var startY = STARTY;
                startY -= header(doc, scratchcos, startY, person);
                var size = infoRenderer.arbeidsforholdOpptjening(arbeidsforhold, scratchcos, startY);
                var behov = startY - size;
                if (behov < y) {
                    scratchcos.close();
                    y = infoRenderer.arbeidsforholdOpptjening(arbeidsforhold, cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }
            if (opptjening.frilans() != null) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var startY = STARTY;
                startY -= header(doc, scratchcos, startY, person);
                var size = infoRenderer.frilansOpptjening(svp.opptjening().frilans(), scratchcos, startY);
                var behov = startY - size;
                if (behov < y) {
                    scratchcos.close();
                    y = infoRenderer.frilansOpptjening(svp.opptjening().frilans(), cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }
            if (!opptjening.egenNæring().isEmpty()) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var startY = STARTY;
                startY -= header(doc, scratchcos, startY, person);
                var size = infoRenderer.egneNæringerOpptjening(opptjening.egenNæring(), scratchcos, startY);
                var behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = infoRenderer.egneNæringerOpptjening(opptjening.egenNæring(), cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }
            if (!opptjening.utenlandskArbeidsforhold().isEmpty()) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var startY = STARTY;
                startY -= header(doc, scratchcos, startY, person);
                var size = infoRenderer.utenlandskeArbeidsforholdOpptjening(
                        opptjening.utenlandskArbeidsforhold(),
                        søknad.getVedlegg(),
                        scratchcos, startY);
                var behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = infoRenderer.utenlandskeArbeidsforholdOpptjening(
                            opptjening.utenlandskArbeidsforhold(),
                            søknad.getVedlegg(), cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }
            if (!svp.avtaltFerie().isEmpty()) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var startY = STARTY;
                startY -= header(doc, scratchcos, startY, person);
                var size = infoRenderer.feriePerioder(svp.avtaltFerie(), scratchcos, startY);
                var behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = infoRenderer.feriePerioder(svp.avtaltFerie(), cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }
            if (svp.utenlandsopphold() != null) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var startY = STARTY;
                startY -= header(doc, scratchcos, startY, person);
                var size = infoRenderer.utenlandsopphold(svp.utenlandsopphold(), scratchcos, startY);
                var behov = startY - size;
                if (behov < y) {
                    scratchcos.close();
                    infoRenderer.utenlandsopphold(svp.utenlandsopphold(), cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                }
            }
            cos.close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UnexpectedInputException("Kunne ikke lage PDF", e);
        }
    }

    private List<EnkeltArbeidsforhold> aktiveArbeidsforhold(LocalDate tidligstDatoForTilrettelegging) {
        return safeStream(arbeidsInfo.hentArbeidsforhold())
            .filter(a -> a.to().isEmpty() || a.to().get().isAfter(tidligstDatoForTilrettelegging))
            .toList();
    }

    private float renderTilrettelegging(List<EnkeltArbeidsforhold> arbeidsgivere,
            Arbeidsforhold arbeidsforhold,
            List<Tilrettelegging> tilrettelegging,
            List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        if (arbeidsforhold instanceof Virksomhet v) {
            var text = virksomhetsnavn(arbeidsgivere, v.orgnr().value())
                .orElse(txt("arbeidsgiverIkkeFunnet", v.orgnr().value()));
            y -= renderer.addLineOfRegularText(text, cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof PrivatArbeidsgiver p) {
            var text = virksomhetsnavn(arbeidsgivere, p.fnr().value())
                .orElse(txt("svp.privatarbeidsgiverNavnIkkeFunnet"));
            y -= renderer.addLineOfRegularText(text, cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof SelvstendigNæringsdrivende s) {
            y -= renderer.addLineOfRegularText(txt("svp.selvstendig"), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer",
                    s.risikoFaktorer()),
                    cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak",
                    s.tilretteleggingstiltak()),
                    cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof Frilanser f) {
            y -= renderer.addLineOfRegularText(txt("svp.frilans"), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer",
                    f.risikoFaktorer()), cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak",
                    f.tilretteleggingstiltak()), cos, y);
            y -= blankLine();
        }
        return y;
    }

    private static List<Tilrettelegging> sortertTilretteleggingsliste(List<Tilrettelegging> liste) {
        return safeStream(liste)
                .sorted(Comparator.comparing(Tilrettelegging::getBehovForTilretteleggingFom))
                .toList();
    }

    private float renderTilretteleggingsperioder(List<Tilrettelegging> perioder,
            List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        var startY = y;
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
        var startY = y;
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.sluttearbeid", DATEFMT.format(periode.getSlutteArbeidFom())), cos, y);
        return startY - y;
    }

    private float renderDelvisTilrettelegging(DelvisTilrettelegging periode, FontAwareCos cos, float y)
            throws IOException {
        var startY = y;
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.tilretteleggingfra", DATEFMT.format(periode.getTilrettelagtArbeidFom())), cos, y);
        y -= renderer.addBulletPoint(DOUBLE_INDENT,
                txt("svp.stillingsprosent", prosentFra(periode.getStillingsprosent())), cos, y);
        return startY - y;
    }

    private static double prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::prosent)
                .orElse(0d);
    }

    private float renderHelTilrettelegging(HelTilrettelegging periode, FontAwareCos cos, float y)
            throws IOException {
        var startY = y;
        y -= renderer.addBulletPoint(INDENT, txt("svp.tilretteleggingfra",
                DATEFMT.format(periode.getTilrettelagtArbeidFom())), cos, y);
        y -= renderer.addBulletPoint(DOUBLE_INDENT, txt("svp.stillingsprosent.full"), cos, y);
        return startY - y;
    }

    private float renderVedlegg(List<Vedlegg> vedlegg, List<VedleggReferanse> vedleggRefs, String keyIfAnnet,
                                FontAwareCos cos, float y) throws IOException {
        var startY = y;
        if (!vedleggRefs.isEmpty()) {
            y -= renderer.addBulletPoint(INDENT, txt("vedlegg1"), cos, y);
        }
        for (var vedleggRef : vedleggRefs) {
            var details = safeStream(vedlegg)
                    .filter(s -> vedleggRef.referanse().equals(s.getId()))
                    .findFirst();
            if (details.isPresent()) {
                var beskrivelse = vedleggsBeskrivelse(keyIfAnnet, details.get());
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

    private Optional<String> virksomhetsnavn(List<EnkeltArbeidsforhold> arbeidsgivere, String arbeidsgiverId) {
        return safeStream(arbeidsgivere)
                .filter(arb -> arb.arbeidsgiverId().equals(arbeidsgiverId))
                .findFirst()
                .map(EnkeltArbeidsforhold::arbeidsgiverNavn);
    }

    private float omBarn(Svangerskapspenger svp, FontAwareCos cos, float y) throws IOException {
        var startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("ombarn"), cos, y);
        if (svp.fødselsdato() != null) {
            y -= renderer.addLineOfRegularText(
                    txt("fødselsdato", DATEFMT.format(svp.fødselsdato())), cos, y);
            y -= renderer.addLineOfRegularText(
                    txt("fødselmedtermin", DATEFMT.format(svp.termindato())), cos, y);
        } else {
            y -= renderer.addLineOfRegularText(
                    txt("svp.termindato", DATEFMT.format(svp.termindato())), cos, y);
        }
        return startY - y;
    }

    private float header(FontAwarePdfDocument doc, FontAwareCos cos, float y, InnsendingPersonInfo person)
            throws IOException {
        var startY = y;
        y -= renderer.addLogo(doc, cos, y);
        y -= renderer.addCenteredHeading(textFormatter.fromMessageSource("svp.søknad"), cos, y);
        y -= renderer.addCenteredRegular(
                textFormatter.fromMessageSource("mottatt", FMT.format(LocalDateTime.now())), cos, y);
        y -= renderer.addCenteredRegulars(søker(person), cos, y);
        y -= renderer.addDividerLine(cos, y);
        return startY - y;
    }

    private List<String> søker(InnsendingPersonInfo person) {
        return Arrays.asList(
                textFormatter.navn(person.navn()),
                textFormatter.fromMessageSource("fødselsnummerinline", person.fnr().value()));
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
