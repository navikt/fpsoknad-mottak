package no.nav.foreldrepenger.mottak.domain;

public interface CorrelationIdGenerator {

    String getOrCreate();

    String create();

    String getKey();

}
