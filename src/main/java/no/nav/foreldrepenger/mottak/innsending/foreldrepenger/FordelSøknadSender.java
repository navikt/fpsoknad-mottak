package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;

@Service
public class FordelSøknadSender implements SøknadSender {

    private static final Logger LOG = LoggerFactory.getLogger(FordelSøknadSender.class);
    private final FordelConnection connection;
    private final KonvoluttGenerator generator;

    public FordelSøknadSender(FordelConnection connection,
                              KonvoluttGenerator generator) {
        this.connection = connection;
        this.generator = generator;
    }

    @Override
    public Kvittering søk(Søknad søknad, SøknadEgenskap egenskap, InnsendingPersonInfo person) {
        return send(generator.generer(søknad, egenskap, person));
    }

    @Override
    public Kvittering endreSøknad(Endringssøknad endring, SøknadEgenskap egenskap, InnsendingPersonInfo person) {
        return send(generator.generer(endring, egenskap, person));
    }

    @Override
    public Kvittering ettersend(Ettersending ettersending, SøknadEgenskap egenskap, InnsendingPersonInfo person) {
        return send(generator.generer(ettersending, egenskap, person.aktørId()));
    }

    Kvittering send(Konvolutt konvolutt) {
        var pdfHovedDokument = konvolutt.PDFHovedDokument();
        var mottattDato = LocalDateTime.now();
        FordelResultat fordelKvittering;
        try {
            fordelKvittering = connection.send(konvolutt);
        } catch (UventetPollingStatusFpFordelException e) {
            LOG.info("Uventet kvittering ved polling på status for innsendt dokument fra fpfordel. Returnerer kvittering uten saksnummer", e);
            fordelKvittering = new FordelResultat(null, null);
        }

        return new Kvittering(mottattDato, fordelKvittering.saksnummer(), pdfHovedDokument);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", generator=" + generator + "]";
    }
}
