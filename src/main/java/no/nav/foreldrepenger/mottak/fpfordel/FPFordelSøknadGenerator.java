package no.nav.foreldrepenger.mottak.fpfordel;

import javax.xml.bind.JAXBContext;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.util.Jaxb;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Brukerroller;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

@Component
public class FPFordelSøknadGenerator {
    private static final JAXBContext CONTEXT = Jaxb.context(Soeknad.class);

    public String toXML(Søknad søknad) {
        return toXML(toFPFordelModel(søknad));
    }

    private static Soeknad toFPFordelModel(Søknad søknad) {
        return new Soeknad()
                .withSoeker(søkerFra(søknad.getSøker()))
                .withMottattDato(søknad.getMottattdato().toLocalDate())
                .withBegrunnelseForSenSoeknad(søknad.getBegrunnelseForSenSøknad())
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger());
    }

    private static Bruker søkerFra(Søker søker) {
        return new Bruker(søker.getAktør().getId(), rolleFra(søker.getSøknadsRolle()), null, null);
    }

    private static Brukerroller rolleFra(BrukerRolle søknadsRolle) {
        switch (søknadsRolle) {
        case MOR:
            return new Brukerroller().withValue("MOR");
        case FAR:
            return new Brukerroller().withValue("FAR");
        case MEDMOR:
            return new Brukerroller().withValue("MEDMOR");
        case ANDRE:
            return new Brukerroller().withValue("ANDRE");
        default:
            throw new IllegalArgumentException("Vil aldri skje");
        }
    }

    public String toXML(Soeknad model) {
        return Jaxb.marshall(CONTEXT, model);
    }

}
