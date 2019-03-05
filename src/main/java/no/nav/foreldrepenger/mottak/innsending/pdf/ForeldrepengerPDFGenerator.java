package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Opptjening;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper.FORELDREPENGER;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

@Component
public class ForeldrepengerPDFGenerator implements PDFGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerPDFGenerator.class);
    private static final float STARTY = PDFElementRenderer.calculateStartY();
    private final Oppslag oppslag;
    private final ForeldrepengeInfoRenderer fpRenderer;

    public ForeldrepengerPDFGenerator(Oppslag oppslag, ForeldrepengeInfoRenderer fpRenderer) {
        this.oppslag = oppslag;
        this.fpRenderer = fpRenderer;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return FORELDREPENGER;
    }

    @Override
    public byte[] generate(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        switch (egenskap.getType()) {
        case INITIELL_FORELDREPENGER:
            return generate(søknad, søker, emptyList());
        case ENDRING_FORELDREPENGER:
            return generate(Endringssøknad.class.cast(søknad), søker);
        default:
            throw new UnexpectedInputException("Ukjent type " + egenskap.getType() + " for søknad, kan ikke lage PDF");
        }
    }

    public byte[] generate(Søknad søknad, Person søker, final List<Arbeidsforhold> arbeidsforhold) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = STARTY;

        try (FontAwarePDDocument doc = new FontAwarePDDocument();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = newPage();
            doc.addPage(page);
            FontAwareCos cos = new FontAwareCos(doc, page);
            float y = yTop;
            y = fpRenderer.header(søker, doc, cos, false, y);
            float headerSize = yTop - y;

            if (stønad.getRelasjonTilBarn() != null) {
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                float size = fpRenderer.relasjonTilBarn(stønad.getRelasjonTilBarn(), søknad.getVedlegg(), scratchcos,
                        startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = fpRenderer.relasjonTilBarn(stønad.getRelasjonTilBarn(), søknad.getVedlegg(), cos, y);
                }
                else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }

            AnnenForelder annenForelder = stønad.getAnnenForelder();
            if (annenForelder != null) {
                y = fpRenderer.annenForelder(annenForelder, stønad.getFordeling().isErAnnenForelderInformert(),
                        stønad.getRettigheter(), cos,
                        y);

            }

            if (søknad.getTilleggsopplysninger() != null) {
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos,
                        true, startY);
                float size = fpRenderer.renderTilleggsopplysninger(søknad.getTilleggsopplysninger(),
                        scratchcos, startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = fpRenderer.renderTilleggsopplysninger(søknad.getTilleggsopplysninger(),
                            cos, y);
                }
                else {
                    cos = nySide(doc, cos, scratch1,
                            scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }

            Opptjening opptjening = stønad.getOpptjening();
            List<Arbeidsforhold> faktiskearbeidsforhold = arbeidsforhold(arbeidsforhold);
            if (opptjening != null) {
                PDPage scratch = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                float size = fpRenderer.arbeidsforholdOpptjening(faktiskearbeidsforhold, scratchcos, startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = fpRenderer.arbeidsforholdOpptjening(faktiskearbeidsforhold, cos, y);
                }
                else {
                    cos = nySide(doc, cos, scratch, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
                if (!opptjening.getUtenlandskArbeidsforhold().isEmpty()) {
                    PDPage scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.utenlandskeArbeidsforholdOpptjening(
                            opptjening.getUtenlandskArbeidsforhold(),
                            søknad.getVedlegg(),
                            scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.utenlandskeArbeidsforholdOpptjening(
                                opptjening.getUtenlandskArbeidsforhold(),
                                søknad.getVedlegg(), cos, y);
                    }
                    else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (!opptjening.getAnnenOpptjening().isEmpty()) {
                    PDPage scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.annenOpptjening(opptjening.getAnnenOpptjening(), søknad.getVedlegg(),
                            scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.annenOpptjening(
                                opptjening.getAnnenOpptjening(),
                                søknad.getVedlegg(), cos, y);
                    }
                    else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (!opptjening.getEgenNæring().isEmpty()) {
                    PDPage scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.egneNæringerOpptjening(opptjening.getEgenNæring(), scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.egneNæringerOpptjening(opptjening.getEgenNæring(), cos, y);
                    }
                    else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (opptjening.getFrilans() != null) {
                    PDPage scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.frilansOpptjening(opptjening.getFrilans(),
                            scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.frilansOpptjening(opptjening.getFrilans(), cos, y);
                    }
                    else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (stønad.getMedlemsskap() != null) {
                    PDPage scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.medlemsskap(stønad.getMedlemsskap(), stønad.getRelasjonTilBarn(), scratchcos,
                            startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.medlemsskap(stønad.getMedlemsskap(), stønad.getRelasjonTilBarn(), cos, y);
                    }
                    else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (stønad.getFordeling() != null) {
                    cos = fpRenderer.fordeling(doc, søker, søknad.getSøker().getSøknadsRolle(), stønad.getFordeling(),
                            stønad.getDekningsgrad(),
                            søknad.getVedlegg(),
                            stønad.getRelasjonTilBarn().getAntallBarn(), false,
                            cos, y);
                }
            }
            cos.close();
            doc.save(baos);
            LOG.trace("Dokumentet er på {} side{}", doc.getNumberOfPages(),
                    doc.getNumberOfPages() > 1 ? "r" : "");
            return baos.toByteArray();

        } catch (Exception e) {
            LOG.warn("Kunne ikke lage PDF", e);
            throw new PDFException("Kunne ikke lage PDF", e);
        }
    }

    private byte[] generate(Endringssøknad søknad, Person søker) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = STARTY;

        try (FontAwarePDDocument doc = new FontAwarePDDocument();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = newPage();
            doc.addPage(page);
            FontAwareCos cos = new FontAwareCos(doc, page);
            float y = yTop;
            y = fpRenderer.header(søker, doc, cos, true,
                    y);
            float headerSize = yTop - y;

            if (stønad.getRelasjonTilBarn() != null) {
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos,
                        true, startY);
                float size = fpRenderer.relasjonTilBarn(stønad.getRelasjonTilBarn(), søknad.getVedlegg(),
                        scratchcos, startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = fpRenderer.relasjonTilBarn(stønad.getRelasjonTilBarn(), søknad.getVedlegg(),
                            cos, y);
                }
                else {
                    cos = nySide(doc, cos, scratch1,
                            scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }

            AnnenForelder annenForelder = stønad.getAnnenForelder();
            if (annenForelder != null) {
                y = fpRenderer.annenForelder(annenForelder,
                        stønad.getFordeling().isErAnnenForelderInformert(), stønad.getRettigheter(),
                        cos, y);
            }

            if (søknad.getTilleggsopplysninger() != null) {
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos,
                        true, startY);
                float size = fpRenderer.renderTilleggsopplysninger(søknad.getTilleggsopplysninger(),
                        scratchcos, startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = fpRenderer.renderTilleggsopplysninger(søknad.getTilleggsopplysninger(),
                            cos, y);
                }
                else {
                    cos = nySide(doc, cos, scratch1,
                            scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }

            if (stønad.getFordeling() != null) {
                cos = fpRenderer.fordeling(doc, søker, søknad.getSøker().getSøknadsRolle(), stønad.getFordeling(),
                        stønad.getDekningsgrad(),
                        søknad.getVedlegg(),
                        stønad.getRelasjonTilBarn().getAntallBarn(), true,
                        cos, y);
            }
            cos.close();
            doc.save(baos);
            LOG.trace("Dokumentet for endring er på {} side{}", doc.getNumberOfPages(),
                    doc.getNumberOfPages() > 1 ? "r" : "");
            return baos.toByteArray();
        } catch (Exception e) {
            LOG.warn("Kunne ikke lage PDF", e);
            throw new PDFException("Kunne ikke lage PDF", e);
        }
    }

    private List<Arbeidsforhold> arbeidsforhold(List<Arbeidsforhold> forhold) {
        return forhold.isEmpty() ? oppslag.getArbeidsforhold() : forhold;
    }

    private static FontAwareCos nySide(PDDocument doc, FontAwareCos cos, PDPage scratch,
            FontAwareCos scratchcos) throws IOException {
        cos.close();
        doc.addPage(scratch);
        return scratchcos;
    }

    private static float nesteSideStart(float headerSize, float behov) {
        return STARTY - behov - headerSize;
    }

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", fpRenderer=" + fpRenderer
                + ", mapperEgenskaper=" + mapperEgenskaper() + "]";
    }

}
