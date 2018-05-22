package no.nav.foreldrepenger.mottak.fpfordel;

import static no.nav.foreldrepenger.mottak.util.Jaxb.marshall;

import javax.xml.bind.JAXBContext;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.util.Jaxb;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Brukerroller;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

@Component
public class FPFordelSøknadGenerator {
    private static final JAXBContext CONTEXT = Jaxb.context(Soeknad.class);

    public String toXML(Søknad søknad, AktorId aktørId) {
        return toXML(toFPFordelModel(søknad, aktørId));
    }

    // TODO mangler mye
    private static Soeknad toFPFordelModel(Søknad søknad, AktorId aktørId) {
        return new Soeknad()
                .withSoeker(søkerFra(aktørId, søknad.getSøker().getSøknadsRolle()))
                .withMottattDato(søknad.getMottattdato().toLocalDate())
                .withBegrunnelseForSenSoeknad(søknad.getBegrunnelseForSenSøknad())
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger());
    }

    // TODO språk og fullmektig ?
    private static Bruker søkerFra(AktorId aktørId, BrukerRolle brukerRolle) {
        return new Bruker(aktørId.getId(), rolleFra(brukerRolle), null, null);
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
        return marshall(CONTEXT, model);
    }

}
