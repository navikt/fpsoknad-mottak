package no.nav.foreldrepenger.mottak.innsyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum BehandlingResultat {
    IKKE_FASTSATT,
    INNVILGET,
    AVSLÅTT,
    OPPHØR,
    HENLAGT_SØKNAD_TRUKKET,
    HENLAGT_FEILOPPRETTET,
    HENLAGT_BRUKER_DØD,
    MERGET_OG_HENLAGT,
    HENLAGT_SØKNAD_MANGLER,
    FORELDREPENGER_ENDRET,
    KLAGE_AVVIST,
    KLAGE_MEDHOLD,
    KLAGE_YTELSESVEDTAK_OPPHEVET,
    KLAGE_YTELSESVEDTAK_STADFESTET,
    HENLAGT_KLAGE_TRUKKET,
    INNSYN_INNVILGET,
    INNSYN_DELVIS_INNVILGET,
    INGEN_ENDRING,
    INNSYN_AVVIST;

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingResultat.class);

    public static BehandlingResultat valueSafelyOf(String name) {
        try {
            return BehandlingResultat.valueOf(name);
        } catch (Exception e) {
            LOG.warn("Ingen enum verdi for {}", name);
            return null;
        }
    }
}
