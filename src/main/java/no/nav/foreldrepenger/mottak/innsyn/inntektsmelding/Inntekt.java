package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import lombok.Data;

@Data
public class Inntekt {

    private final Double beløp;
    private final BeregnetInntektEndringsÅrsak endringsårsak;

}
