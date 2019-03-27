package no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ArbeidType {
    ORDINÆRT_ARBEID, ANNET, SELVSTENDIG_NÆRINGSDRIVENDE, FRILANS;

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidType.class);

    public static ArbeidType valueSafelyOf(String name) {
        try {
            return ArbeidType.valueOf(name);
        } catch (Exception e) {
            LOG.warn("Ingen enum verdi for {}", name);
            return null;
        }
    }
}
