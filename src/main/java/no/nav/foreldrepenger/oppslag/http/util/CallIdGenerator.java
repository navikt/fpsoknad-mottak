package no.nav.foreldrepenger.oppslag.http.util;

public interface CallIdGenerator {

    String getOrCreate();

    String create();

    String getKey();

}
