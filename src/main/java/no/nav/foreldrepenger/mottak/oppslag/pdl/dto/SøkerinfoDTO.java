package no.nav.foreldrepenger.mottak.oppslag.pdl.dto;

import java.util.List;

import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

public class SøkerinfoDTO {

    private final SøkerDTO søker;
    private final List<EnkeltArbeidsforhold> arbeidsforhold;

    public SøkerinfoDTO(SøkerDTO søker, List<EnkeltArbeidsforhold> arbeidsforhold) {
        this.søker = søker;
        this.arbeidsforhold = arbeidsforhold;
    }

    public SøkerDTO getSøker() {
        return søker;
    }

    public List<EnkeltArbeidsforhold> getArbeidsforhold() {
        return arbeidsforhold;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [søker=" + søker + ", arbeidsforhold=" + arbeidsforhold + "]";
    }
}
