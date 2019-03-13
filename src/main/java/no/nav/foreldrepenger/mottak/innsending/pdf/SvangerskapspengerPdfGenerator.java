package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_SVP_VERSJON;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

@Service
public class SvangerskapspengerPdfGenerator implements PDFGenerator {

    @Override
    public byte[] generate(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        final PDPage page = new PDPage(A4);
        try {
            FontAwarePDDocument doc = new FontAwarePDDocument();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.addPage(page);
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new PDFException("Kunne ikke lage PDF", e);
        }

    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return new MapperEgenskaper(DEFAULT_SVP_VERSJON, INITIELL_SVANGERSKAPSPENGER);
    }

}
