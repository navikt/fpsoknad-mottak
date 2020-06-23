package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import static no.nav.foreldrepenger.mottak.util.MapUtil.get;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public class OrganisasjonMapper {

    private static final String NAVN = "navn";
    private static final String NAVNELINJE1 = "navnelinje1";
    private static final String NAVNELINJE2 = "navnelinje2";
    private static final String NAVNELINJE3 = "navnelinje3";
    private static final String NAVNELINJE4 = "navnelinje4";
    private static final String NAVNELINJE5 = "navnelinje5";

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonMapper.class);

    public static String map(Map<?, ?> map, String orgnr) {
        try {
            var navn = get(map, NAVN, Map.class);
            var navn1 = get(navn, NAVNELINJE1);
            var navn2 = get(navn, NAVNELINJE2);
            var navn3 = get(navn, NAVNELINJE3);
            var navn4 = get(navn, NAVNELINJE4);
            var navn5 = get(navn, NAVNELINJE5);
            LOG.info("{} Fikk navn 1={} 2={} 3={} 4={} 5={} ({})", orgnr, navn1, navn2, navn3, navn4, navn5);
            return Joiner.on(", ")
                    .skipNulls()
                    .join(navn1, navn2, navn3, navn4, navn5);
        } catch (Exception e) {
            LOG.warn("OOPS", e);
            return "UKJENT";
        }
    }
}
