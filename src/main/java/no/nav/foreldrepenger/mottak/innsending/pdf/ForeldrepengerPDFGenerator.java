package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.isDevOrPreprod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fordeling;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Opptjening;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.RelasjonTilBarnMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Rettigheter;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@Component
public class ForeldrepengerPDFGenerator implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerPDFGenerator.class);
    private final Oppslag oppslag;
    private final ForeldrepengeInfoRenderer fpRenderer;

    private final PDFElementRenderer pdfRenderer;
    private Environment env;

    @Inject
    public ForeldrepengerPDFGenerator(@Qualifier("landkoder") MessageSource landkoder,
            @Qualifier("kvitteringstekster") MessageSource kvitteringstekster,
            Oppslag oppslag) {
        this(oppslag, new PDFElementRenderer(), new ForeldrepengeInfoRenderer(landkoder, kvitteringstekster));
    }

    private ForeldrepengerPDFGenerator(Oppslag oppslag, PDFElementRenderer pdfRenderer,
            ForeldrepengeInfoRenderer fpRenderer) {
        this.oppslag = oppslag;
        this.pdfRenderer = pdfRenderer;
        this.fpRenderer = fpRenderer;
    }

    public byte[] generate(Søknad søknad, Person søker) {
        return generate(søknad, søker, true);
    }

    public byte[] generate(Søknad søknad, Person søker, boolean doLookup) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = PDFElementRenderer.calculateStartY();

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page1 = pdfRenderer.newPage();
            doc.addPage(page1);
            try {
                PDPageContentStream cos = new PDPageContentStream(doc, page1);
                float y = yTop;
                y -= fpRenderer.header(søker, doc, cos, false, y);
                AnnenForelder annenForelder = stønad.getAnnenForelder();
                if (annenForelder != null) {
                    y -= fpRenderer.annenForelder(annenForelder, stønad.getFordeling().isErAnnenForelderInformert(),
                            stønad.getRettigheter().isHarAnnenForelderRett(), cos,
                            y);
                }

                if (stønad.getDekningsgrad() != null) {
                    y -= fpRenderer.dekningsgrad(stønad.getDekningsgrad(), cos, y);
                }

                Opptjening opptjening = stønad.getOpptjening();
                if (opptjening != null) {
                    if (isDevOrPreprod(env) && !doLookup) {
                        System.out.println("y now " + y);
                        LOG.trace("Slår IKKE opp arbeidforhold");
                        PDPage scratch = pdfRenderer.newPage();
                        PDPageContentStream dummycos = new PDPageContentStream(doc, scratch);
                        float startY = PDFElementRenderer.calculateStartY();
                        startY -= fpRenderer.header(søker, doc, dummycos, false, startY);
                        startY -= fpRenderer.opptjening(opptjening, dummyArbeidsforhold(), søknad.getVedlegg(),
                                dummycos,
                                startY);
                        float spaceRequired = PDFElementRenderer.calculateStartY() - startY;
                        if (spaceRequired <= y) {
                            System.out.println(spaceRequired + "<" + y);
                            y -= fpRenderer.opptjening(opptjening, dummyArbeidsforhold(), søknad.getVedlegg(), cos, y);
                        }
                        else {
                            System.out.println(spaceRequired + ">=" + y);
                            cos.close();
                            doc.addPage(scratch);
                            cos = dummycos;
                            y = startY;
                        }
                    }
                    else {
                        y -= fpRenderer.opptjening(opptjening, oppslag.getArbeidsforhold(), søknad.getVedlegg(), cos,
                                y);
                    }
                }
                cos.close();
            } catch (IOException ex) {
                throw new RuntimeException("Error while creating pdf", ex);
            }

            PDPage page2 = pdfRenderer.newPage();
            doc.addPage(page2);
            try {
                PDPageContentStream cos = new PDPageContentStream(doc, page2);
                float y = yTop;
                y -= fpRenderer.header(søker, doc, cos, false, y);

                Medlemsskap medlemsskap = stønad.getMedlemsskap();
                if (medlemsskap != null) {
                    y -= fpRenderer.medlemsskap(medlemsskap, cos, y);
                }
                RelasjonTilBarnMedVedlegg relasjon = stønad.getRelasjonTilBarn();
                if (relasjon != null) {
                    y -= fpRenderer.relasjonTilBarn(relasjon, søknad.getVedlegg(), cos, y);
                }
                Fordeling fordeling = stønad.getFordeling();
                if (fordeling != null) {
                    PDPage scratch = pdfRenderer.newPage();
                    PDPageContentStream dummycos = new PDPageContentStream(doc, scratch);
                    float startY = PDFElementRenderer.calculateStartY();
                    startY -= fpRenderer.header(søker, doc, dummycos, false, startY);
                    startY -= fpRenderer.fordeling(fordeling, dummycos, startY);
                    float spaceRequired = PDFElementRenderer.calculateStartY() - startY;
                    if (spaceRequired <= y) {
                        y -= fpRenderer.fordeling(fordeling, cos, y);
                    }
                    else {
                        cos.close();
                        doc.addPage(scratch);
                        cos = dummycos;
                        y = startY;
                    }
                }
                Rettigheter rettigheter = stønad.getRettigheter();
                if (rettigheter != null) {
                    PDPage scratch = pdfRenderer.newPage();
                    PDPageContentStream dummycos = new PDPageContentStream(doc, scratch,
                            PDPageContentStream.AppendMode.APPEND,
                            false, false);
                    float startY = PDFElementRenderer.calculateStartY();
                    startY -= fpRenderer.header(søker, doc, dummycos, false, startY);
                    startY -= fpRenderer.rettigheter(rettigheter, dummycos, startY);
                    float spaceRequired = PDFElementRenderer.calculateStartY() - startY;
                    if (spaceRequired <= y) {
                        y -= fpRenderer.rettigheter(rettigheter, cos, y);
                    }
                    else {
                        cos.close();
                        doc.addPage(scratch);
                        cos = dummycos;
                        y = startY;
                    }
                }
                final List<Vedlegg> vedlegg = søknad.getVedlegg();
                if (vedlegg != null) {
                    fpRenderer.vedlegg(søknad.getVedlegg(), cos, y);
                }
                cos.close();
                doc.save(baos);
                return baos.toByteArray();
            } catch (IOException ex) {
                throw new RuntimeException("Error while creating pdf", ex);
            }

        } catch (IOException ex) {
            throw new RuntimeException("Error while creating pdf", ex);
        }
    }

    public byte[] generate(Endringssøknad søknad, Person søker) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = PDFElementRenderer.calculateStartY();

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = pdfRenderer.newPage();
            try (PDPageContentStream cos = new PDPageContentStream(doc, page)) {
                float y = yTop;
                y -= fpRenderer.header(søker, doc, cos, true, y);

                AnnenForelder annenForelder = stønad.getAnnenForelder();
                if (annenForelder != null) {
                    y -= fpRenderer.annenForelder(annenForelder, stønad.getFordeling().isErAnnenForelderInformert(),
                            stønad.getRettigheter().isHarAnnenForelderRett(), cos,
                            y);
                }

                RelasjonTilBarnMedVedlegg relasjon = stønad.getRelasjonTilBarn();
                if (relasjon != null) {
                    y -= fpRenderer.relasjonTilBarn(relasjon, søknad.getVedlegg(), cos, y);
                }

                Fordeling fordeling = stønad.getFordeling();
                if (fordeling != null) {
                    y -= fpRenderer.fordeling(fordeling, cos, y);
                }

                Rettigheter rettigheter = stønad.getRettigheter();
                if (rettigheter != null) {
                    y -= fpRenderer.rettigheter(rettigheter, cos, y);
                }
                final List<Vedlegg> vedlegg = søknad.getVedlegg();
                if (vedlegg != null && !vedlegg.isEmpty()) {
                    fpRenderer.vedlegg(søknad.getVedlegg(), cos, y);
                }
                doc.addPage(page);
            } catch (IOException ex) {
                throw new RuntimeException("Error while creating pdf", ex);
            }
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error while creating pdf", ex);
        }
    }

    private static List<Arbeidsforhold> dummyArbeidsforhold() {
        return Lists.newArrayList(new Arbeidsforhold("1234", "", LocalDate.now().minusDays(200),
                Optional.of(LocalDate.now()), 90.0, "El Bedrifto"),
                new Arbeidsforhold("5678", "", LocalDate.now().minusDays(100),
                        Optional.of(LocalDate.now()), 80.0, "TGD"));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", fpRenderer=" + fpRenderer + ", pdfRenderer="
                + pdfRenderer + ", env=" + env + "]";
    }
}
