package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import java.net.URI;
import java.time.Duration;

import org.apache.commons.lang3.time.StopWatch;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPSakFordeltKvittering;

public interface SaksStatusPoller {

    Kvittering poll(URI locationFra, String ref, StopWatch timer, Duration duration, FPSakFordeltKvittering cast);

}
