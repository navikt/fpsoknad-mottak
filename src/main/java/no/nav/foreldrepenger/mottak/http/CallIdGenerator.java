package no.nav.foreldrepenger.mottak.http;

import no.nav.foreldrepenger.mottak.domain.Pair;

public interface CallIdGenerator {

    Pair<String, String> generateCallId();

    String generateCallId(String key);

    String getDefaultKey();

}
