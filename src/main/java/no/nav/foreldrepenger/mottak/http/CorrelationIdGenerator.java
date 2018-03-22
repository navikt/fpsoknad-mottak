package no.nav.foreldrepenger.mottak.http;

public interface CorrelationIdGenerator {

    String getOrCreate();

    String create();

    String getKey();

}
