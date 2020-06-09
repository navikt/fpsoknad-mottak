package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrganisasjonMapper {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonMapper.class);

    public static String map(Map<?, ?> map) {
        LOG.info("Mapper fra {}", map);
        return "hello world";
    }

}
