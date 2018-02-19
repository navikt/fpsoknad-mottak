package no.nav.foreldrepenger.mottak.domain;

public interface CallIdGenerator {

    Pair<String, String> generateCallId();

    String generateCallId(String key);

    String getDefaultKey();

}
