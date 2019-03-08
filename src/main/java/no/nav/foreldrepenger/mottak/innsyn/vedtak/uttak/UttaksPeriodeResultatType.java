package no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum UttaksPeriodeResultatType {
    INNVILGET;

    private static final Logger LOG = LoggerFactory.getLogger(UttaksPeriodeResultatType.class);

    public static UttaksPeriodeResultatType valueSafelyOf(String name) {
        try {
            return UttaksPeriodeResultatType.valueOf(name);
        } catch (Exception e) {
            LOG.warn("INgen enum verdi for {}", name);
            return null;
        }
    }
}
