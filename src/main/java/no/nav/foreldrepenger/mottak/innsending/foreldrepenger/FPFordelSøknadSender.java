package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Service
public class FPFordelSøknadSender implements SøknadSender {

    private final FPFordelConnection connection;
    private final FPFordelKonvoluttGenerator payloadGenerator;

    public FPFordelSøknadSender(FPFordelConnection connection, FPFordelKonvoluttGenerator payloadGenerator) {
        this.connection = connection;
        this.payloadGenerator = payloadGenerator;
    }

    @Override
    public Kvittering søk(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return doSend(egenskap.getType(), payloadGenerator.payload(søknad, søker, egenskap));
    }

    @Override
    public Kvittering endreSøknad(Endringssøknad endringsSøknad, Person søker, SøknadEgenskap egenskap) {
        return doSend(egenskap.getType(), payloadGenerator.payload(endringsSøknad, søker, egenskap));
    }

    @Override
    public Kvittering ettersend(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        return doSend(egenskap.getType(), payloadGenerator.payload(ettersending, søker));
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    private Kvittering doSend(SøknadType type, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        return connection.send(type, payload);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", payloadGenerator=" + payloadGenerator
                + "]";
    }

}
