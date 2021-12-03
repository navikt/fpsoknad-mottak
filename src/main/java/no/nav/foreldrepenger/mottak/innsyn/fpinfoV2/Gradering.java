package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import java.math.BigDecimal;

record Gradering(Arbeidstidprosent arbeidstidprosent) {

    Gradering(BigDecimal arbeidstidprosent) {
        this(new Arbeidstidprosent(arbeidstidprosent));
    }

    static record Arbeidstidprosent(BigDecimal value) implements SerializableValue {
    }
}
