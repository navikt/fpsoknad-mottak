package no.nav.foreldrepenger.mottak.fpfordel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;

@Service
public class FPFordelSøknadSender implements SøknadSender {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelSøknadSender.class);

    private final FPFordelConnection connection;

    public FPFordelSøknadSender(FPFordelConnection connection) {
        this.connection = connection;
    }

    public void ping() {
        connection.ping();
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
