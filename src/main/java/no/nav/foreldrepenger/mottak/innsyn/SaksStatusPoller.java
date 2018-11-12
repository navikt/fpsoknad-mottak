package no.nav.foreldrepenger.mottak.innsyn;

import java.net.URI;
import java.time.Duration;

import org.apache.commons.lang3.time.StopWatch;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPSakFordeltKvittering;

public interface SaksStatusPoller {

    Kvittering poll(URI locationFra, StopWatch timer, Duration duration, FPSakFordeltKvittering cast);

}
