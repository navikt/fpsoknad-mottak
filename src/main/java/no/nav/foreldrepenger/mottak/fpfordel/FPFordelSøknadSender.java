package no.nav.foreldrepenger.mottak.fpfordel;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;

@Service
public class FPFordelSøknadSender implements SøknadSender {

    private final FPFordelConnection connection;

    public FPFordelSøknadSender(FPFordelConnection connection) {
        this.connection = connection;
    }

    @Override
    public Kvittering sendSøknad(Søknad søknad) {
        if (connection.isEnabled()) {

        }
        return Kvittering.IKKE_SENDT;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + "]";
    }

}
