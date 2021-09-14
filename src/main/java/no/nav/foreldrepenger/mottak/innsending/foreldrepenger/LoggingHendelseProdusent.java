package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Kvittering;

@Component
@ConditionalOnProperty(value = "mottak.sender.domainevent.enabled", havingValue = "false")
public class LoggingHendelseProdusent implements InnsendingHendelseProdusent {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingHendelseProdusent.class);

    @Override
    public void publiser(Fødselsnummer fnr, Kvittering kvittering, String referanseId, Konvolutt konvolutt) {
        LOG.info(
                "Publiserer hendelse fra {} for søknad av type {} med opplastede vedlegg {}, ikkeopplastede vedlegg {} og referanseId {}",
                kvittering, konvolutt.getType(),
                konvolutt.getOpplastedeVedlegg(), konvolutt.getIkkeOpplastedeVedlegg(), referanseId);
    }

}
