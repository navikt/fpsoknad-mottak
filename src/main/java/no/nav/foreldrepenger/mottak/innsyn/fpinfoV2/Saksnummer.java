package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import java.util.Objects;

record Saksnummer(String value) implements SerializableValue {

    Saksnummer {
        Objects.requireNonNull(value, "saksnummer kan ikke være null");
    }
}
