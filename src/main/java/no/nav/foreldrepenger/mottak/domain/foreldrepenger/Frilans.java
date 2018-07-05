package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Frilans {

    private final ÅpenPeriode periode;
    private final boolean harInntektFraFosterhjem;
    private final boolean nyOppstartet;
    private final boolean nærRelasjon;
    private final List<FrilansOppdrag> frilansOppdrag;

    @JsonCreator
    public Frilans(@JsonProperty("periode") ÅpenPeriode periode,
            @JsonProperty("harInntektFraFosterhjem") boolean harInntektFraFosterhjem,
            @JsonProperty("nyOppstartet") boolean nyOppstartet,
            @JsonProperty("nærRelasjon") boolean nærRelasjon,
            @JsonProperty("frilansOppdrag") List<FrilansOppdrag> frilansOppdrag) {
        this.periode = periode;
        this.harInntektFraFosterhjem = harInntektFraFosterhjem;
        this.nyOppstartet = nyOppstartet;
        this.nærRelasjon = nærRelasjon;
        this.frilansOppdrag = Optional.ofNullable(frilansOppdrag).orElse(emptyList());
    }
}
