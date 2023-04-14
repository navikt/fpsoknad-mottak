package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.util.MDCUtil;

@Component
@ConditionalOnProperty(value = "mottak.sender.domainevent.enabled", havingValue = "false")
public class LoggingHendelseProdusent implements InnsendingHendelseProdusent {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingHendelseProdusent.class);

    @Override
    public void publiser(FordelResultat kvittering, String dialogId, Konvolutt konvolutt, InnsendingPersonInfo person) {
        var callId = MDCUtil.callId();
        LOG.info(
                "Publiserer hendelse fra {} for søknad av type {} med opplastede vedlegg {}, ikkeopplastede vedlegg {} og referanseId {}",
                kvittering, konvolutt.getType(),
                konvolutt.getOpplastedeVedlegg(), konvolutt.getIkkeOpplastedeVedlegg(), callId);
    }

}
