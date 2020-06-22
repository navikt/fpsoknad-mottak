package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import static no.nav.foreldrepenger.mottak.util.MapUtil.get;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrganisasjonMapper {

    private static final String REDIGERTNAVN = "redigertnavn";
    private static final String NAVNELINJE1 = "navnelinje1";
    private static final String NAVNELINJE2 = "navnelinje2";
    private static final String NAVNELINJE3 = "navnelinje3";
    private static final String NAVNELINJE4 = "navnelinje4";
    private static final String NAVNELINJE5 = "navnelinje5";

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonMapper.class);
    private static final String NAVN = "navn";

    public static String map(Map<?, ?> map) {
        try {
            var navn = get(map, NAVN, Map.class);
            String navn1 = get(navn, NAVNELINJE1);
            String navn2 = get(navn, NAVNELINJE2);
            String navn3 = get(navn, NAVNELINJE3);
            String navn4 = get(navn, NAVNELINJE4);
            String navn5 = get(navn, NAVNELINJE5);
            String normalisertNavn = get(navn, REDIGERTNAVN);
            LOG.info("Fikk navn 1={} 2={} 3={} 4={} 5={} ({})", navn1, navn2, navn3, navn4, navn5, normalisertNavn);
            return navn1;
        } catch (Exception e) {
            LOG.warn("OOPS ", e);
            return "UKJENT";
        }
    }
}
