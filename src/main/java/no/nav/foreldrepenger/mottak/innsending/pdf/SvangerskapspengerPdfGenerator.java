package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.VedleggReferanse;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Tilretteleggingbehov;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.arbeidsforhold.Frilanser;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.arbeidsforhold.PrivatArbeidsgiver;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.arbeidsforhold.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.arbeidsforhold.Virksomhet;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;
import no.nav.foreldrepenger.mottak.oversikt.EnkeltArbeidsforhold;
import no.nav.foreldrepenger.mottak.oversikt.Oversikt;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000049;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000060;
import static no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper.SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.innsending.pdf.SvangerskapspengerHelper.virksomhetsnavn;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

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
    private final Oversikt arbeidsInfo;

    @Autowired
    public SvangerskapspengerPdfGenerator(PdfElementRenderer renderer,
                                          SøknadTextFormatter textFormatter,
                                          Oversikt arbeidsInfo,
                                          SvangerskapspengerInfoRenderer infoRenderer) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
        this.arbeidsInfo = arbeidsInfo;
        this.infoRenderer = infoRenderer;
    }

    @Override
    public byte[] generer(Søknad søknad, SøknadEgenskap egenskap, InnsendingPersonInfo person) {
        var svp = (Svangerskapspenger) søknad.getYtelse();
        var aktiveArbeidsforhold = aktiveArbeidsforhold(svp.getTidligstDatoForTilrettelegging());
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
            if (!svp.tilretteleggingbehov().isEmpty()) {
                y -= renderer.addLeftHeading(textFormatter.fromMessageSource("tilrettelegging"), cos, y);
                var sorterteTilrettelegginger = svp.tilretteleggingbehov().stream().sorted(Comparator.comparing(Tilretteleggingbehov::behovForTilretteleggingFom)).toList();
                for (var tilretteleggingbehov : sorterteTilrettelegginger) {
                    var scratch1 = newPage();
                    var scratchcos = new FontAwareCos(doc, scratch1);
                    var startY = STARTY;
                    startY -= header(doc, scratchcos, startY, person);
                    var size = renderTilretteleggingen(aktiveArbeidsforhold, tilretteleggingbehov, søknad.getVedlegg(), scratchcos, startY);
                    var behov = startY - size;
                    if (behov < y) {
                        scratchcos.close();
                        y = renderTilretteleggingen(aktiveArbeidsforhold, tilretteleggingbehov, søknad.getVedlegg(), cos, y);
                    } else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }
            }
            if (!aktiveArbeidsforhold.isEmpty()) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var startY = STARTY;
                startY -= header(doc, scratchcos, startY, person);
                var size = infoRenderer.arbeidsforholdOpptjening(aktiveArbeidsforhold, scratchcos, startY);
                var behov = startY - size;
                if (behov < y) {
                    scratchcos.close();
                    y = infoRenderer.arbeidsforholdOpptjening(aktiveArbeidsforhold, cos, y);
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
            if (!svp.avtaltFerie().isEmpty()) { // TODO: skal rendres uansett?
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var startY = STARTY;
                startY -= header(doc, scratchcos, startY, person);
                var size = infoRenderer.feriePerioder(aktiveArbeidsforhold, svp.avtaltFerie(), scratchcos, startY);
                var behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = infoRenderer.feriePerioder(aktiveArbeidsforhold, svp.avtaltFerie(), cos, y);
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

    private float renderTilretteleggingen(List<EnkeltArbeidsforhold> arbeidsgivere,
                                          Tilretteleggingbehov tilretteleggingbehov,
                                          List<Vedlegg> vedlegg, FontAwareCos cos, float y)throws IOException {
        var arbeidsforhold = tilretteleggingbehov.arbeidsforhold();
        if (arbeidsforhold instanceof Virksomhet(Orgnummer orgnr)) {
            var text = virksomhetsnavn(arbeidsgivere, orgnr.value())
                .orElse(txt("arbeidsgiverIkkeFunnet", orgnr.value()));
            y -= renderer.addLineOfRegularText(text, cos, y);
            y -= renderTilretteleggingsperiodene(tilretteleggingbehov, vedlegg, cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof PrivatArbeidsgiver(Fødselsnummer fnr)) {
            var text = virksomhetsnavn(arbeidsgivere, fnr.value())
                .orElse(txt("svp.privatarbeidsgiverNavnIkkeFunnet"));
            y -= renderer.addLineOfRegularText(text, cos, y);
            y -= renderTilretteleggingsperiodene(tilretteleggingbehov, vedlegg, cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof SelvstendigNæringsdrivende(String risikoFaktorer, String tilretteleggingstiltak)) {
            y -= renderer.addLineOfRegularText(txt("svp.selvstendig"), cos, y);
            y -= renderTilretteleggingsperiodene(tilretteleggingbehov, vedlegg, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer", risikoFaktorer), cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak", tilretteleggingstiltak), cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof Frilanser(String risikoFaktorer, String tilretteleggingstiltak)) {
            y -= renderer.addLineOfRegularText(txt("svp.frilans"), cos, y);
            y -= renderTilretteleggingsperiodene(tilretteleggingbehov, vedlegg, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer", risikoFaktorer), cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak", tilretteleggingstiltak), cos, y);
            y -= blankLine();
        }
        return y;
    }

    private float renderTilretteleggingsperiodene(Tilretteleggingbehov tilretteleggingbehov, List<Vedlegg> vedlegg, FontAwareCos cos, float y) throws IOException {
        var startY = y;
        y -= renderer.addBulletPoint(INDENT,
            txt("svp.behovfra", DATEFMT.format(tilretteleggingbehov.behovForTilretteleggingFom())), cos, y);
        for (var periode : tilretteleggingbehov.tilrettelegginger()) {
            if (periode instanceof Tilretteleggingbehov.Tilrettelegging.Hel h) {
                y -= renderHelTilrettelegging(h, cos, y);
            } else if (periode instanceof Tilretteleggingbehov.Tilrettelegging.Delvis d) {
                y -= renderDelvisTilrettelegging(d, cos, y);
            } else if (periode instanceof Tilretteleggingbehov.Tilrettelegging.Ingen i) {
                y -= renderIngenTilrettelegging(i, cos, y);
            }
        }

        y -= renderVedlegg(vedlegg, tilretteleggingbehov.vedlegg(), SVP_VEDLEGG_TILRETTELEGGING, cos, y);
        return startY - y;
    }

    private float renderIngenTilrettelegging(Tilretteleggingbehov.Tilrettelegging.Ingen periode, FontAwareCos cos, float y)
        throws IOException {
        var startY = y;
        y -= renderer.addBulletPoint(INDENT,
            txt("svp.sluttearbeid", DATEFMT.format(periode.fom())), cos, y);
        return startY - y;
    }

    private float renderDelvisTilrettelegging(Tilretteleggingbehov.Tilrettelegging.Delvis periode, FontAwareCos cos, float y)
        throws IOException {
        var startY = y;
        y -= renderer.addBulletPoint(INDENT,
            txt("svp.tilretteleggingfra", DATEFMT.format(periode.fom())), cos, y);
        y -= renderer.addBulletPoint(DOUBLE_INDENT,
            txt("svp.stillingsprosent", periode.stillingsprosent()), cos, y);
        return startY - y;
    }


    private float renderHelTilrettelegging(Tilretteleggingbehov.Tilrettelegging.Hel periode, FontAwareCos cos, float y)
        throws IOException {
        var startY = y;
        y -= renderer.addBulletPoint(INDENT, txt("svp.tilretteleggingfra",
            DATEFMT.format(periode.fom())), cos, y);
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
