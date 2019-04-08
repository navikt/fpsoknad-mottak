package no.nav.foreldrepenger.mottak.innsyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum BehandlingStatus {
    AVSLUTTET,
    FATTER_VEDTAK,
    IVERKSETTER_VEDTAK,
    OPPRETTET,
    UTREDES;

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingStatus.class);

    public static BehandlingStatus valueSafelyOf(String name) {
        try {
            return BehandlingStatus.valueOf(name);
        } catch (Exception e) {
            LOG.warn("Ingen enum verdi for {}", name);
            return null;
        }
    }
}
