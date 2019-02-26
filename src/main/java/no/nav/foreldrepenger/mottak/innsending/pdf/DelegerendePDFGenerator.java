package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;

@Component
public class DelegerendePDFGenerator {

    private final ForeldrepengerPDFGenerator fpGenerator;
    private final EngangsstønadPDFGenerator esGenerator;

    public DelegerendePDFGenerator(ForeldrepengerPDFGenerator fpGenerator, EngangsstønadPDFGenerator esGenerator) {
        this.fpGenerator = fpGenerator;
        this.esGenerator = esGenerator;
    }

    public byte[] generate(Søknad søknad, Person søker, SøknadType type) {
        switch (type) {
        case INITIELL_FORELDREPENGER:
            return fpGenerator.generate(søknad, søker);
        case INITIELL_ENGANGSSTØNAD:
            return esGenerator.generate(søknad, søker);
        default:
            throw new UnexpectedInputException("Uventet type " + type + " for søknad");
        }
    }

    public byte[] generate(Endringssøknad endringssøknad, Person søker, SøknadType type) {
        switch (type) {
        case ENDRING_FORELDREPENGER:
            return fpGenerator.generate(endringssøknad, søker);
        default:
            throw new UnexpectedInputException("Uventet type " + type + " for endringssøknad");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fpGenerator=" + fpGenerator + ", esGenerator=" + esGenerator + "]";
    }
}
