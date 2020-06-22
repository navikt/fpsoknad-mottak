package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import static no.nav.foreldrepenger.mottak.util.MapUtil.get;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrganisasjonMapper {

    private static final String NAVNELINJE1 = "navnelinje1";
    private static final String NAVN = "navn";

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonMapper.class);

    public static String map(Map<?, ?> map) {
        try {
            return get(get(map, NAVN, Map.class), NAVNELINJE1);
        } catch (Exception e) {
            LOG.warn("OOPS ", e);
            return "UKJENT";
        }
    }
}
