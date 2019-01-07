package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.util.Collections.emptyList;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Opptjening;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@Component
public class ForeldrepengerPDFGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerPDFGenerator.class);
    private static final float STARTY = PDFElementRenderer.calculateStartY();
    private final Oppslag oppslag;
    private final ForeldrepengeInfoRenderer fpRenderer;

    public ForeldrepengerPDFGenerator(Oppslag oppslag, ForeldrepengeInfoRenderer fpRenderer) {
        this.oppslag = oppslag;
        this.fpRenderer = fpRenderer;
    }

    public byte[] generate(Søknad søknad, Person søker) {
        return generate(søknad, søker, emptyList());
    }

    public byte[] generate(Søknad søknad, Person søker, final List<Arbeidsforhold> arbeidsforhold) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = STARTY;

        try (FontAwarePDDocument doc = new FontAwarePDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = newPage();
            doc.addPage(page);
            FontAwareCos cos = new FontAwareCos(doc, page);
            float y = yTop;
            LOG.trace("Y ved start {}", y);
            y = fpRenderer.header(søker, doc, cos, false, y);
            float headerSize = yTop - y;
            LOG.trace("Heaader trenger  {}", headerSize);

            if (stønad.getRelasjonTilBarn() != null) {
                LOG.trace("Y før relasjon til barn {}", y);
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                float size = fpRenderer.relasjonTilBarn(stønad.getRelasjonTilBarn(), søknad.getVedlegg(), scratchcos,
                        startY);
                float behov = startY - size;
                if (behov <= y) {
                    LOG.trace("Nok plass til relasjon til barn, trenger {}, har {}", behov, y);
                    scratchcos.close();
                    y = fpRenderer.relasjonTilBarn(stønad.getRelasjonTilBarn(), søknad.getVedlegg(), cos, y);
                }
                else {
                    LOG.trace("Trenger ny side. IKKE nok plass til relasjon på side {}, trenger {}, har {}",
                            doc.getNumberOfPages(), behov, y);
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
                LOG.trace("Y etter relasjon til barn {}", y);
            }

            AnnenForelder annenForelder = stønad.getAnnenForelder();
            if (annenForelder != null) {
                LOG.trace("Y før annen  forelder {}", y);
                y = fpRenderer.annenForelder(annenForelder, stønad.getFordeling().isErAnnenForelderInformert(),
                        stønad.getRettigheter(), cos,
                        y);
                LOG.trace("Y etter annen forelder {}", y);

            }

            Opptjening opptjening = stønad.getOpptjening();
            List<Arbeidsforhold> faktiskearbeidsforhold = arbeidsforhold(arbeidsforhold);
            if (opptjening != null) {
                LOG.trace("Y før opptjening {}", y);
                PDPage scratch = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                float size = fpRenderer.arbeidsforholdOpptjening(faktiskearbeidsforhold, scratchcos, startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    LOG.trace("Nok plass til opptjening, trenger {}, har {}", behov, y);
                    y = fpRenderer.arbeidsforholdOpptjening(faktiskearbeidsforhold, cos, y);
                }
                else {
                    LOG.trace("Trenger ny side. IKKE nok plass til opptjening på side {}, trenger {}, har {}",
                            doc.getNumberOfPages(),
                            behov, y);
                    cos = nySide(doc, cos, scratch, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
                if (!opptjening.getUtenlandskArbeidsforhold().isEmpty()) {
                    LOG.trace("Y før utenlandsk arbeidsforhold {}", y);
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
                        LOG.trace("Nok plass til utenlandsk arbeidsforhold, trenger {}, har {}", behov, y);
                        scratchcos.close();
                        y = fpRenderer.utenlandskeArbeidsforholdOpptjening(
                                opptjening.getUtenlandskArbeidsforhold(),
                                søknad.getVedlegg(), cos, y);
                    }
                    else {
                        LOG.trace(
                                "Trenger ny side. IKKE nok plass til utenlandsk arbeidsforhold på side {}, trenger {}, har {}",
                                doc.getNumberOfPages(),
                                behov, y);
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (!opptjening.getAnnenOpptjening().isEmpty()) {
                    LOG.trace("Y før annen opptjening {}", y);
                    PDPage scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.annenOpptjening(opptjening.getAnnenOpptjening(), søknad.getVedlegg(),
                            scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        LOG.trace("Nok plass til annen opptjening, trenger {}, har {}", behov, y);
                        scratchcos.close();
                        y = fpRenderer.annenOpptjening(
                                opptjening.getAnnenOpptjening(),
                                søknad.getVedlegg(), cos, y);
                    }
                    else {
                        LOG.trace(
                                "Trenger ny side. IKKE nok plass til annen opptjening på side {}, trenger {}, har {}",
                                doc.getNumberOfPages(),
                                behov,
                                y);
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (!opptjening.getEgenNæring().isEmpty()) {
                    LOG.trace("Y før egen næring {}", y);
                    PDPage scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.egneNæringerOpptjening(opptjening.getEgenNæring(), scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        LOG.trace("Nok plass til egen næring, trenger {}, har {}", behov, y);
                        scratchcos.close();
                        y = fpRenderer.egneNæringerOpptjening(opptjening.getEgenNæring(), cos, y);
                    }
                    else {
                        LOG.trace(
                                "Trenger ny side. IKKE nok plass til egen næring på side {}, trenger {}, har {}",
                                doc.getNumberOfPages(),
                                behov, y);
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (opptjening.getFrilans() != null) {
                    LOG.trace("Y før frilans {}", y);
                    PDPage scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.frilansOpptjening(opptjening.getFrilans(),
                            scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        LOG.trace("Nok plass til frilans, trenger {}, har {}", behov, y);
                        scratchcos.close();
                        y = fpRenderer.frilansOpptjening(opptjening.getFrilans(), cos, y);
                    }
                    else {
                        LOG.trace("Trenger ny side. IKKE nok plass til frilans på side {}, trenger {}, har {}",
                                doc.getNumberOfPages(),
                                behov, y);
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (stønad.getMedlemsskap() != null) {
                    LOG.trace("Y før medlemsskap {}", y);
                    PDPage scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.medlemsskap(stønad.getMedlemsskap(), stønad.getRelasjonTilBarn(), scratchcos,
                            startY);
                    behov = startY - size;
                    if (behov <= y) {
                        LOG.trace("Nok plass til medlemsskap, trenger {}, har {}", behov, y);
                        scratchcos.close();
                        y = fpRenderer.medlemsskap(stønad.getMedlemsskap(), stønad.getRelasjonTilBarn(), cos, y);
                    }
                    else {
                        LOG.trace(
                                "Trenger ny side. IKKE nok plass til medlemsskap på side {}, trenger {}, har {}",
                                doc.getNumberOfPages(),
                                behov, y);
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (stønad.getFordeling() != null) {
                    cos = fpRenderer.fordeling(doc, søker, stønad.getFordeling(), stønad.getDekningsgrad(),
                            søknad.getVedlegg(),
                            stønad.getRelasjonTilBarn().getAntallBarn(), false,
                            cos, y);
                }
            }
            cos.close();
            doc.save(baos);
            LOG.info("Dokumentet før søknad er på {} side{}", doc.getNumberOfPages(),
                    doc.getNumberOfPages() > 1 ? "r" : "");
            return baos.toByteArray();

        } catch (IOException e) {
            LOG.warn("Kunne ikke lage PDF", e);
            throw new PDFException("Kunne ikke lage PDF", e);
        }
    }

    public byte[] generate(Endringssøknad søknad, Person søker) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = STARTY;

        try (FontAwarePDDocument doc = new FontAwarePDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = newPage();
            doc.addPage(page);
            FontAwareCos cos = new FontAwareCos(doc, page);
            float y = yTop;
            LOG.trace("Y ved start {}", y);
            y = fpRenderer.header(søker, doc, cos, true,
                    y);
            float headerSize = yTop - y;
            LOG.trace("Heaader trenger  {}", headerSize);

            if (stønad.getRelasjonTilBarn() != null) {
                LOG.trace("Y før relasjon til barn {}", y);
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos,
                        true, startY);
                float size = fpRenderer.relasjonTilBarn(stønad.getRelasjonTilBarn(), søknad.getVedlegg(),
                        scratchcos, startY);
                float behov = startY - size;
                if (behov <= y) {
                    LOG.trace("Nok plass til relasjon til barn, trenger {}, har {}", behov, y);
                    scratchcos.close();
                    y = fpRenderer.relasjonTilBarn(stønad.getRelasjonTilBarn(), søknad.getVedlegg(),
                            cos, y);
                }
                else {
                    LOG.trace("Trenger ny side. IKKE nok plass til relasjon på side {}, trenger {}, har {}",
                            doc.getNumberOfPages(), behov, y);
                    cos = nySide(doc, cos, scratch1,
                            scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
                LOG.trace("Y etter relasjon til barn {}", y);

            }

            AnnenForelder annenForelder = stønad.getAnnenForelder();
            if (annenForelder != null) {
                LOG.trace("Y før annen  forelder {}", y);
                y = fpRenderer.annenForelder(annenForelder,
                        stønad.getFordeling().isErAnnenForelderInformert(), stønad.getRettigheter(),
                        cos, y);
                LOG.trace("Y etter forelder {}", y);

            }

            if (stønad.getFordeling() != null) {
                LOG.trace("Y før fordeling {}", y);
                cos = fpRenderer.fordeling(doc, søker, stønad.getFordeling(), stønad.getDekningsgrad(),
                        søknad.getVedlegg(),
                        stønad.getRelasjonTilBarn().getAntallBarn(), true,
                        cos, y);
                LOG.trace("Y etter fordeling {}", y);

            }
            cos.close();
            doc.addPage(page);
            doc.save(baos);
            LOG.info("Dokumentet for endring er på {} side{}", doc.getNumberOfPages(),
                    doc.getNumberOfPages() > 1 ? "r" : "");
            return baos.toByteArray();
        } catch (IOException e) {
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
        cos = scratchcos;
        return cos;
    }

    private static float nesteSideStart(float headerSize, float behov) {
        return STARTY - behov - headerSize;
    }

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", fpRenderer=" + fpRenderer + "]";
    }
}
