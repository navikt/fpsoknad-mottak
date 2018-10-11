package no.nav.foreldrepenger.mottak.innsending.pdf;

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
import no.nav.foreldrepenger.mottak.util.EnvUtil;

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
            LOG.trace("Page dimensions in points:  {}x{}", page1.getMediaBox().getHeight(),
                    page1.getMediaBox().getWidth());
            try (PDPageContentStream cos = new PDPageContentStream(doc, page1)) {
                float y = yTop;
                LOG.trace("Y at start {}", y);
                y = header(søker, doc, cos, y);
                LOG.trace("Y after header {}", y);
                y = annenForelder(stønad, cos, y);
                LOG.trace("Y after annenForelder {}", y);
                y = dekningsgrad(stønad, cos, y);
                LOG.trace("Y after dekningsgrad {}", y);
                y = opptjeniing(doLookup, stønad, cos, y);
                LOG.trace("Y after opptjeniing {}", y);
                doc.addPage(page1);
            } catch (IOException ex) {
                throw new RuntimeException("Error while creating pdf", ex);
            }

            PDPage page2 = pdfRenderer.newPage();
            try (PDPageContentStream cos = new PDPageContentStream(doc, page2)) {
                float y = yTop;
                y -= header(søker, doc, cos, y);

                Medlemsskap medlemsskap = stønad.getMedlemsskap();
                if (medlemsskap != null) {
                    y -= fpRenderer.medlemsskap(medlemsskap, cos, y);
                }
                RelasjonTilBarnMedVedlegg relasjon = stønad.getRelasjonTilBarn();
                if (relasjon != null) {
                    y -= fpRenderer.relasjonTilBarn(relasjon, cos, y);
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
                if (vedlegg != null) {
                    fpRenderer.vedlegg(søknad.getVedlegg(), cos, y);
                }

                doc.addPage(page2);
            } catch (IOException ex) {
                throw new RuntimeException("Error while creating pdf", ex);
            }

            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error while creating pdf", ex);
        }
    }

    private float header(Person søker, PDDocument doc, PDPageContentStream cos, float y) throws IOException {
        return fpRenderer.header(søker, doc, cos, false, y);
    }

    private float opptjeniing(boolean doLookup, Foreldrepenger stønad, PDPageContentStream cos, float y)
            throws IOException {
        Opptjening opptjening = stønad.getOpptjening();
        if (opptjening != null) {
            if (EnvUtil.isDevOrPreprod(env) && !doLookup) {
                LOG.trace("Slår ikke opp arbeidforhold, håper dette var fra Swagger");
                y -= fpRenderer.opptjening(opptjening, dummyArbeidsforhold(), cos, y);
            }
            else {
                y -= fpRenderer.opptjening(opptjening, oppslag.getArbeidsforhold(), cos, y);
            }
        }
        return y;
    }

    private float dekningsgrad(Foreldrepenger stønad, PDPageContentStream cos, float y) throws IOException {
        if (stønad.getDekningsgrad() != null) {
            y -= fpRenderer.dekningsgrad(stønad.getDekningsgrad(), cos, y);
        }
        return y;
    }

    private float annenForelder(Foreldrepenger stønad, PDPageContentStream cos, float y) throws IOException {
        AnnenForelder annenForelder = stønad.getAnnenForelder();
        if (annenForelder != null) {
            y -= fpRenderer.annenForelder(annenForelder, stønad.getFordeling().isErAnnenForelderInformert(),
                    stønad.getRettigheter().isHarAnnenForelderRett(), cos, y);
        }
        return y;
    }

    public byte[] generate(Endringssøknad søknad, Person søker) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = PDFElementRenderer.calculateStartY();

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = pdfRenderer.newPage();
            try (PDPageContentStream cos = new PDPageContentStream(doc, page)) {
                float y = yTop;
                y -= fpRenderer.header(søker, doc, cos, true, y);

                y = annenForelder(stønad, cos, y);

                RelasjonTilBarnMedVedlegg relasjon = stønad.getRelasjonTilBarn();
                if (relasjon != null) {
                    y -= fpRenderer.relasjonTilBarn(relasjon, cos, y);
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
                Optional.of(LocalDate.now()), 90.0, "El Bedrifto"));
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
