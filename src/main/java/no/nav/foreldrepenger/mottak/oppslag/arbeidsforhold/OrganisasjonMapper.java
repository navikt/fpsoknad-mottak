package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static no.nav.foreldrepenger.mottak.util.MapUtil.get;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

class OrganisasjonMapper {

    private static final String NAVN = "navn";
    private static final String NAVNELINJE1 = "navnelinje1";
    private static final String NAVNELINJE2 = "navnelinje2";
    private static final String NAVNELINJE3 = "navnelinje3";
    private static final String NAVNELINJE4 = "navnelinje4";
    private static final String NAVNELINJE5 = "navnelinje5";

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonMapper.class);

    static String map(Map<?, ?> map) {
        try {
            var navn = get(map, NAVN, Map.class);
            return Joiner.on(", ")
                    .skipNulls()
                    .join(get(navn, NAVNELINJE1),
                            get(navn, NAVNELINJE2),
                            get(navn, NAVNELINJE3),
                            get(navn, NAVNELINJE4),
                            get(navn, NAVNELINJE5));
        } catch (Exception e) {
            LOG.warn("OOPS", e);
            return null;
        }
    }
}
