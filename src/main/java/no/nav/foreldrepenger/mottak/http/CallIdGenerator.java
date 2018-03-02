package no.nav.foreldrepenger.mottak.http;

public interface CallIdGenerator {

    String getOrCreate();

    String create();

    String getKey();

}
