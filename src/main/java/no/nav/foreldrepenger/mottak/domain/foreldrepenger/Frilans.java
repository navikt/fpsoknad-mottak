package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = "vedlegg")
public class Frilans {

    private final ÅpenPeriode periode;
    private final boolean harInntektFraFosterhjem;
    private final boolean nyOppstartet;
    private final List<FrilansOppdrag> frilansOppdrag;
    private final List<String> vedlegg;

    @JsonCreator
    public Frilans(@JsonProperty("periode") ÅpenPeriode periode,
            @JsonProperty("harInntektFraFosterhjem") boolean harInntektFraFosterhjem,
            @JsonProperty("nyOppstartet") boolean nyOppstartet,
            @JsonProperty("frilansOppdrag") List<FrilansOppdrag> frilansOppdrag,
            @JsonProperty("vedlegg") List<String> vedlegg) {
        this.periode = periode;
        this.harInntektFraFosterhjem = harInntektFraFosterhjem;
        this.nyOppstartet = nyOppstartet;
        this.frilansOppdrag = Optional.ofNullable(frilansOppdrag).orElse(emptyList());
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
    }
}
