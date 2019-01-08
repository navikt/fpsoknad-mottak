package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.util.Versjon.V2;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.util.Versjon;

public interface SøknadSender {
    String DOKMOT_SENDER = "dokmot";
    String FPFORDEL_SENDER = "fpfordel";
    String ROUTING_SENDER = "routing";
    Versjon DEFAULT_VERSJON = V2;

    Kvittering send(Søknad søknad, Person søker, Versjon versjon);

    Kvittering send(Ettersending ettersending, Person søker, Versjon versjon);

    Kvittering send(Endringssøknad endringsøknad, Person søker, Versjon versjon);

    default Kvittering send(Søknad søknad, Person søker) {
        return send(søknad, søker, DEFAULT_VERSJON);
    }

    default Kvittering send(Ettersending søknad, Person søker) {
        return send(søknad, søker, DEFAULT_VERSJON);
    }

    default Kvittering send(Endringssøknad søknad, Person søker) {
        return send(søknad, søker, DEFAULT_VERSJON);
    }

}
