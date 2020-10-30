package no.nav.foreldrepenger.mottak.oppslag.dkif;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum Målform {
    @JsonEnumDefaultValue
    nb,
    nn,
    e;

    public static Målform def() {
        return nb;
    }
}
