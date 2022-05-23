package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper.FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.pdf.PdfOutlineItem.FORELDREPENGER_OUTLINE;
import static no.nav.foreldrepenger.mottak.util.CollectionUtil.tryOrEmpty;
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

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
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
        return FORELDREPENGER;
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

            if (stønad.relasjonTilBarn() != null) {
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                float size = fpRenderer.relasjonTilBarn(stønad.relasjonTilBarn(), søknad.getVedlegg(), scratchcos,
                        startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = fpRenderer.relasjonTilBarn(stønad.relasjonTilBarn(), søknad.getVedlegg(), cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }

            var annenForelder = stønad.annenForelder();
            if (annenForelder != null) {
                y = fpRenderer.annenForelder(annenForelder, stønad.fordeling().erAnnenForelderInformert(),
                        stønad.rettigheter(), cos, y);
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

            Opptjening opptjening = stønad.opptjening();
            var arbeidsforhold = aktiveArbeidsforhold(stønad.relasjonTilBarn().relasjonsDato());
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
                if (!opptjening.utenlandskArbeidsforhold().isEmpty()) {
                    var scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.utenlandskeArbeidsforholdOpptjening(
                            opptjening.utenlandskArbeidsforhold(),
                            søknad.getVedlegg(),
                            scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.utenlandskeArbeidsforholdOpptjening(
                                opptjening.utenlandskArbeidsforhold(),
                                søknad.getVedlegg(), cos, y);
                    } else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (!opptjening.annenOpptjening().isEmpty()) {
                    var scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.annenOpptjening(opptjening.annenOpptjening(), søknad.getVedlegg(),
                            scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.annenOpptjening(
                                opptjening.annenOpptjening(),
                                søknad.getVedlegg(), cos, y);
                    } else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (!opptjening.egenNæring().isEmpty()) {
                    var scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.egneNæringerOpptjening(opptjening.egenNæring(), scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.egneNæringerOpptjening(opptjening.egenNæring(), cos, y);
                    } else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (opptjening.frilans() != null) {
                    var scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.frilansOpptjening(opptjening.frilans(),
                            scratchcos, startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.frilansOpptjening(opptjening.frilans(), cos, y);
                    } else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (stønad.medlemsskap() != null) {
                    var scratch1 = newPage();
                    scratchcos = new FontAwareCos(doc, scratch1);
                    startY = STARTY;
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    size = fpRenderer.medlemsskap(stønad.medlemsskap(), stønad.relasjonTilBarn(), scratchcos,
                            startY);
                    behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        y = fpRenderer.medlemsskap(stønad.medlemsskap(), stønad.relasjonTilBarn(), cos, y);
                    } else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }

                if (stønad.fordeling() != null) {
                    cos = fpRenderer.fordeling(doc, søker, søknad.getSøker().getSøknadsRolle(), stønad.fordeling(),
                            stønad.dekningsgrad(),
                            søknad.getVedlegg(),
                            stønad.relasjonTilBarn().getAntallBarn(), false,
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
        return tryOrEmpty(arbeidsforhold::hentArbeidsforhold).stream()
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

            if (stønad.relasjonTilBarn() != null) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY = fpRenderer.header(søker, doc, scratchcos,
                        true, startY);
                float size = fpRenderer.relasjonTilBarn(stønad.relasjonTilBarn(), søknad.getVedlegg(),
                        scratchcos, startY);
                float behov = startY - size;
                if (behov <= y) {
                    scratchcos.close();
                    y = fpRenderer.relasjonTilBarn(stønad.relasjonTilBarn(), søknad.getVedlegg(),
                            cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1,
                            scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }

            var annenForelder = stønad.annenForelder();
            if (annenForelder != null) {
                y = fpRenderer.annenForelder(annenForelder,
                        stønad.fordeling().erAnnenForelderInformert(), stønad.rettigheter(),
                        cos, y);
            }

            var tilleggsopplysninger = søknad.getTilleggsopplysninger();
            if (tilleggsopplysninger != null && !"".equals(tilleggsopplysninger)) {
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

            if (stønad.fordeling() != null) {
                cos = fpRenderer.fordeling(doc, søker, søknad.getSøker().getSøknadsRolle(), stønad.fordeling(),
                        stønad.dekningsgrad(),
                        søknad.getVedlegg(),
                        stønad.relasjonTilBarn().getAntallBarn(), true,
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
