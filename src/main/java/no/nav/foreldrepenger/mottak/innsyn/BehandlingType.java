package no.nav.foreldrepenger.mottak.innsyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum BehandlingType {
    FP,
    ES,
    ENDRING_FP,
    SVP;

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingType.class);

    public static BehandlingType valueSafelyOf(String name) {
        try {
            return BehandlingType.valueOf(name);
        } catch (Exception e) {
            LOG.warn("Ingen enum verdi for {}", name);
            return null;
        }
    }
}
