package no.nav.foreldrepenger.mottak.domain;

public record AktørId(String id) {

    public static AktørId valueOf(String id) {
        return new AktørId(id);
    }
}
