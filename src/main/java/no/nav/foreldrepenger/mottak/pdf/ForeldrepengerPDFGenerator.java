package no.nav.foreldrepenger.mottak.pdf;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.*;
import no.nav.foreldrepenger.mottak.http.Oppslag;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ForeldrepengerPDFGenerator {
    private Oppslag oppslag;
    private ForeldrepengeInfoRenderer fpRenderer;
    private PDFElementRenderer pdfRenderer;

    @Inject
    public ForeldrepengerPDFGenerator(MessageSource landkoder, MessageSource kvitteringstekster, Oppslag oppslag) {
        this.oppslag = oppslag;
        this.pdfRenderer = new PDFElementRenderer();
        this.fpRenderer = new ForeldrepengeInfoRenderer(landkoder, kvitteringstekster);
    }

    public byte[] generate(Søknad søknad, Person søker) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());

        float yTop = PDFElementRenderer.calculateStartY();

        try (PDDocument doc = new PDDocument();
            ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page1 = pdfRenderer.newPage();
            try (PDPageContentStream cos = new PDPageContentStream(doc, page1)) {
                float y = yTop;
                y -= fpRenderer.header(søker, doc, cos, y);

                AnnenForelder annenForelder = stønad.getAnnenForelder();
                if (annenForelder != null) {
                    y -= fpRenderer.annenForelder(annenForelder, stønad.getRettigheter().isHarAnnenForelderRett(), cos, y);
                }

                y -= fpRenderer.dekningsgrad(stønad.getDekningsgrad(), cos, y);

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
                y -= fpRenderer.header(søker, doc, cos, y);

                y -= fpRenderer.medlemsskap(stønad.getMedlemsskap(), cos, y);

                RelasjonTilBarnMedVedlegg relasjon = stønad.getRelasjonTilBarn();
                if (relasjon != null) {
                    y -= fpRenderer.relasjonTilBarn(stønad.getRelasjonTilBarn(), cos, y);
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



}
