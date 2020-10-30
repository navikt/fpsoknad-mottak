package no.nav.foreldrepenger.mottak.oppslag.dkif;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum Målform {
    @JsonEnumDefaultValue
    NB,
    NN,
    E;

    public static Målform def() {
        return NB;
    }
}
