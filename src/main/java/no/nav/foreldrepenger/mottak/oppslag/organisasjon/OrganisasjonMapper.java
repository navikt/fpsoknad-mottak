package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import static no.nav.foreldrepenger.mottak.util.MapUtil.get;

import java.util.Map;

public class OrganisasjonMapper {

    private static final String REDIGERTNAVN = "redigertnavn";
    private static final String NAVN = "navn";

    public static String map(Map<?, ?> map) {
        return get(get(map, NAVN, Map.class), REDIGERTNAVN);
    }

}
