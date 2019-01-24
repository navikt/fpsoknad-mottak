package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Naturalytelse {

    private final NaturalytelseType type;
    private final Double beløpPrMåned;
    private final LocalDate fom;

}
