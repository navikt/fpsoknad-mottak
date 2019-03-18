package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper.SVANGERSKAPSPENGER;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Service
public class SvangerskapspengerPDFGenerator implements PDFGenerator {

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
            throw new UnexpectedInputException("Kunne ikke lage PDF", e);
        }

    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return SVANGERSKAPSPENGER;
    }

}
