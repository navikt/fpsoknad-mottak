package no.nav.foreldrepenger.mottak.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class KjentForelder extends AnnenForelder {

    private final boolean lever;
    private final Navn navn;

    public abstract boolean hasId();

}
