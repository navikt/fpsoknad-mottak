package no.nav.foreldrepenger.oppslag.http.util;

public interface CallIdGenerator {

    Pair<String, String> generateCallId();

    String generateCallId(String key);

    String getDefaultKey();

}
