package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK;

import java.net.URI;
import java.time.Duration;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPSakFordeltKvittering;

//@Service
public class NonPollingSaksPoller implements SaksStatusPoller {

    private static final Logger LOG = LoggerFactory.getLogger(NonPollingSaksPoller.class);

    @Override
    public Kvittering poll(URI uri, String ref, StopWatch timer, Duration delay,
            FPSakFordeltKvittering fordeltKvittering) {
        LOG.debug("This poller does not poll");
        FPSakFordeltKvittering fordelt = FPSakFordeltKvittering.class.cast(fordeltKvittering);
        return kvitteringMedType(SENDT_OG_FORSØKT_BEHANDLET_FPSAK,
                ref,
                fordelt.getJournalpostId(),
                fordelt.getSaksnummer());
    }

    private static Kvittering kvitteringMedType(LeveranseStatus type, String ref, String journalId, String saksnr) {
        Kvittering kvittering = new Kvittering(type, ref);
        kvittering.setJournalId(journalId);
        kvittering.setSaksNr(saksnr);
        return kvittering;
    }
}
