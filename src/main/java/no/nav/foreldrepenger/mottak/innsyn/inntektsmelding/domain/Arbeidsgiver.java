package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.domain;

import lombok.Data;

@Data
public class Arbeidsgiver {

    private final String virksomhetsnummer;
    private final KontaktInformasjon kontaktinformasjon;

}
