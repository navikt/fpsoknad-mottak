package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Avsender {

    private final String navn;
    private final String versjon;
    private final LocalDateTime tidspunkt;
}
