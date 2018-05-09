package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import lombok.Data;

@Data
public class Gradering {
    private final double arbeidstidProsent;
    private final boolean erArbeidstaker;
    private final String virksomhetsNummer;
    private final boolean arbeidsForholdSomskalGraderes;
}
