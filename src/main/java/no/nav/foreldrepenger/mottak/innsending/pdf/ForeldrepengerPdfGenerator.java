package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper.ALLE_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.pdf.PdfOutlineItem.FORELDREPENGER_OUTLINE;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@Component
public class ForeldrepengerPdfGenerator implements MappablePdfGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerPdfGenerator.class);
    private static final float STARTY = PdfElementRenderer.calculateStartY();
    private final Oppslag oppslag;
    private final ArbeidsInfo arbeidsforhold;

    private final ForeldrepengeInfoRenderer fpRenderer;
    private final InfoskrivRenderer infoskrivRenderer;

    public ForeldrepengerPdfGenerator(Oppslag oppslag, ArbeidsInfo arbeidsforhold,
            ForeldrepengeInfoRenderer fpRenderer,
            InfoskrivRenderer infoskrivRenderer) {
        this.oppslag = oppslag;
        this.arbeidsforhold = arbeidsforhold;
        this.fpRenderer = fpRenderer;
        this.infoskrivRenderer = infoskrivRenderer;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return ALLE_FORELDREPENGER;
    }

    @Override
    public byte[] generer(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return switch (egenskap.getType()) {
            case INITIELL_FORELDREPENGER -> generer(søknad, søker);
            case ENDRING_FORELDREPENGER -> generer(Endringssøknad.class.cast(søknad), søker);
            default -> throw new UnexpectedInputException("Ukjent type " + egenskap.getType() + " for søknad, kan ikke lage PDF");
        };

    }

    private byte[] generer(Søknad søknad, Person søker) {
        var stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = STARTY;

        try (var doc = new FontAwarePdfDocument();
                var baos = new ByteArrayOutputStream()) {
            PDPage page = newPage();
            doc.addPage(page);

            fpRenderer.addOutlineItem(doc, page, FORELDREPENGER_OUTLINE);

            var cos = new FontAwareCos(doc, page);
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
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }

            var annenForelder = stønad.getAnnenForelder();
            if (annenForelder != null) {
                y = fpRenderer.annenForelder(annenForelder, stønad.getFordeling().isErAnnenForelderInformert(),
                        stønad.getRettigheter(), cos, y);
            }

            if (søknad.getTilleggsopplysninger() != null) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos, true, startY);
                float size = fpRenderer.renderTilleggsopplysninger(søknad.getTilleggsopplysninger(), scratchcos,
                        startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = fpRenderer.renderTilleggsopplysninger(søknad.getTilleggsopplysninger(), cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }

            Opptjening opptjening = stønad.getOpptjening();
            List<EnkeltArbeidsforhold> arbeidsforhold = aktiveArbeidsforhold(
                    stønad.getRelasjonTilBarn().relasjonsDato());
            if (opptjening != null) {
                var scratch = newPage();
                var scratchcos = new FontAwareCos(doc, scratch);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                float size = fpRenderer.arbeidsforholdOpptjening(arbeidsforhold, scratchcos, startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = fpRenderer.arbeidsforholdOpptjening(arbeidsforhold, cos, y);
                } else {
                    cos = nySide(doc, cos, scratch, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
                if (!opptjening.getUtenlandskArbeidsforhold().isEmpty()) {
                    var scratch1 = newPage();
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
                    } else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (!opptjening.getAnnenOpptjening().isEmpty()) {
                    var scratch1 = newPage();
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
                    } else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (!opptjening.getEgenNæring().isEmpty()) {
                    var scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.egneNæringerOpptjening(opptjening.getEgenNæring(), scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.egneNæringerOpptjening(opptjening.getEgenNæring(), cos, y);
                    } else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (opptjening.getFrilans() != null) {
                    var scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.frilansOpptjening(opptjening.getFrilans(),
                            scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.frilansOpptjening(opptjening.getFrilans(), cos, y);
                    } else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (stønad.getMedlemsskap() != null) {
                    var scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.medlemsskap(stønad.getMedlemsskap(), stønad.getRelasjonTilBarn(), scratchcos,
                            startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.medlemsskap(stønad.getMedlemsskap(), stønad.getRelasjonTilBarn(), cos, y);
                    } else {
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

                if (!arbeidsforhold.isEmpty()) {
                    cos = infoskrivRenderer.renderInfoskriv(arbeidsforhold, søker, søknad, cos, doc);
                }
            }
            cos.close();
            doc.save(baos);
            LOG.trace("Dokumentet er på {} side{}", doc.getNumberOfPages(),
                    doc.getNumberOfPages() > 1 ? "r" : "");
            return baos.toByteArray();

        } catch (Exception e) {
            LOG.warn("Kunne ikke lage PDF", e);
            throw new UnexpectedInputException("Kunne ikke lage PDF", e);
        }
    }

    private List<EnkeltArbeidsforhold> aktiveArbeidsforhold(LocalDate relasjonsdato) {
        return safeStream(arbeidsforhold.hentAktiveArbeidsforhold())
                .filter(a -> a.getTo().isEmpty() || (a.getTo().isPresent() && a.getTo().get().isAfter(relasjonsdato)))
                .collect(Collectors.toList());
    }

    private byte[] generer(Endringssøknad søknad, Person søker) {
        var stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = STARTY;

        try (var doc = new FontAwarePdfDocument();
                var baos = new ByteArrayOutputStream()) {
            var page = newPage();
            doc.addPage(page);
            var cos = new FontAwareCos(doc, page);
            float y = yTop;
            y = fpRenderer.header(søker, doc, cos, true,
                    y);
            float headerSize = yTop - y;

            if (stønad.getRelasjonTilBarn() != null) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
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
                } else {
                    cos = nySide(doc, cos, scratch1,
                            scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }

            var annenForelder = stønad.getAnnenForelder();
            if (annenForelder != null) {
                y = fpRenderer.annenForelder(annenForelder,
                        stønad.getFordeling().isErAnnenForelderInformert(), stønad.getRettigheter(),
                        cos, y);
            }

            if (søknad.getTilleggsopplysninger() != null) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
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
                } else {
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
            throw new UnexpectedInputException("Kunne ikke lage PDF", e);
        }
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
