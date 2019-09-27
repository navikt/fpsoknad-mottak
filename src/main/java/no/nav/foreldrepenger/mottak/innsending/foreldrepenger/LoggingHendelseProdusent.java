package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;

@Component
@ConditionalOnProperty(value = "mottak.sender.domainevent.enabled", havingValue = "false")
public class LoggingHendelseProdusent implements InnsendingHendelseProdusent {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingHendelseProdusent.class);

    @Override
    public void publiser(Kvittering kvittering, String referanseId, SøknadType type, List<String> vedlegg) {
        LOG.info("Publiserer hendelse fra {} for søknad av type {} med vedlegg {} og referanseId {}", kvittering, type,
                vedlegg, referanseId);
    }

}
