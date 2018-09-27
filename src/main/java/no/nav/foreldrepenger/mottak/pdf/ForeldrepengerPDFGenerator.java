package no.nav.foreldrepenger.mottak.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

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
import no.nav.foreldrepenger.mottak.http.Oppslag;

@Component
public class ForeldrepengerPDFGenerator {
    private Oppslag oppslag;
    private ForeldrepengeInfoRenderer fpRenderer;
    private PDFElementRenderer pdfRenderer;

    @Inject
    public ForeldrepengerPDFGenerator(@Qualifier("landkoder") MessageSource landkoder,
            @Qualifier("kvitteringstekster") MessageSource kvitteringstekster,
            Oppslag oppslag) {
        this.oppslag = oppslag;
        this.pdfRenderer = new PDFElementRenderer();
        this.fpRenderer = new ForeldrepengeInfoRenderer(landkoder, kvitteringstekster);
    }

    public byte[] generate(Søknad søknad, Person søker) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = PDFElementRenderer.calculateStartY();

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page1 = pdfRenderer.newPage();
            try (PDPageContentStream cos = new PDPageContentStream(doc, page1)) {
                float y = yTop;
                y -= fpRenderer.header(søker, doc, cos, false, y);

                AnnenForelder annenForelder = stønad.getAnnenForelder();
                if (annenForelder != null) {
                    y -= fpRenderer.annenForelder(annenForelder, stønad.getRettigheter().isHarAnnenForelderRett(), cos,
                            y);
                }

                if (stønad.getDekningsgrad() != null) {
                    y -= fpRenderer.dekningsgrad(stønad.getDekningsgrad(), cos, y);
                }
                Opptjening opptjening = stønad.getOpptjening();
                if (opptjening != null) {
                    List<Arbeidsforhold> arbeidsforhold = oppslag.getArbeidsforhold();
                    fpRenderer.opptjening(opptjening, arbeidsforhold, cos, y);
                }

                doc.addPage(page1);
            } catch (IOException ex) {
                throw new RuntimeException("Error while creating pdf", ex);
            }

            PDPage page2 = pdfRenderer.newPage();
            try (PDPageContentStream cos = new PDPageContentStream(doc, page2)) {
                float y = yTop;
                y -= fpRenderer.header(søker, doc, cos, false, y);

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

    public byte[] generate(Endringssøknad søknad, Person søker) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = PDFElementRenderer.calculateStartY();

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page1 = pdfRenderer.newPage();
            try (PDPageContentStream cos = new PDPageContentStream(doc, page1)) {
                float y = yTop;
                y -= fpRenderer.header(søker, doc, cos, true, y);

                AnnenForelder annenForelder = stønad.getAnnenForelder();
                if (annenForelder != null) {
                    y -= fpRenderer.annenForelder(annenForelder, stønad.getRettigheter().isHarAnnenForelderRett(), cos,
                            y);
                }

                Rettigheter rettigheter = stønad.getRettigheter();
                if (rettigheter != null) {
                    y -= fpRenderer.rettigheter(rettigheter, cos, y);
                }

                RelasjonTilBarnMedVedlegg relasjon = stønad.getRelasjonTilBarn();
                if (relasjon != null) {
                    y -= fpRenderer.relasjonTilBarn(relasjon, cos, y);
                }

                Fordeling fordeling = stønad.getFordeling();
                if (fordeling != null) {
                    y -= fpRenderer.fordeling(fordeling, cos, y);
                }

                final List<Vedlegg> vedlegg = søknad.getVedlegg();
                if (vedlegg != null) {
                    fpRenderer.vedlegg(søknad.getVedlegg(), cos, y);
                }
                doc.addPage(page1);
                doc.save(baos);
                return baos.toByteArray();
            } catch (IOException ex) {
                throw new RuntimeException("Error while creating pdf", ex);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error while creating pdf", ex);
        }
    }
}
