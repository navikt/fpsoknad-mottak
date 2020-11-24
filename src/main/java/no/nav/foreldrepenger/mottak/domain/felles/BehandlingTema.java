package no.nav.foreldrepenger.mottak.domain.felles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum BehandlingTema {
    FORP_FODS,
    FORP_ADOP,
    ENGST_FODS,
    ENGST_ADOP,
    FORP,
    ENGST;

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingTema.class);

    public static BehandlingTema valueSafelyOf(String name) {
        try {
            return BehandlingTema.valueOf(name);
        } catch (Exception e) {
            LOG.trace("Ingen enum verdi for {}", name);
            return null;
        }
    }
}
