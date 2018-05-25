package no.nav.foreldrepenger.mottak.fpfordel;

import java.time.Period;

public class FPFordelPendingKvittering extends FPFordelKvittering {

    static final String STATUS = "PENDING";
    private final Period pollInterval;

    public Period getPollInterval() {
        return pollInterval;
    }

    public FPFordelPendingKvittering(Period pollInterval) {
        super(STATUS);
        this.pollInterval = pollInterval;
    }

}
