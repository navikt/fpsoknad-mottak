package no.nav.foreldrepenger.mottak.domain;

import lombok.Data;

@Data
public abstract class KjentForelder extends AnnenForelder {

    private final boolean lever;

}
