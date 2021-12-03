package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import java.util.Objects;

record AktørId(String value) implements SerializableValue {

    public AktørId {
        Objects.requireNonNull(value, "aktørId kan ikke være null");
    }
}
