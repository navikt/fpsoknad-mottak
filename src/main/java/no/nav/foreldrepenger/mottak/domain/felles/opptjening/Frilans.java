package no.nav.foreldrepenger.mottak.domain.felles.opptjening;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.ÅpenPeriode;

@Data
@ToString(exclude = "vedlegg")
@EqualsAndHashCode(exclude = "vedlegg")
public class Frilans {

    private final ÅpenPeriode periode;
    private final boolean harInntektFraFosterhjem;
    private final boolean nyOppstartet;
    @Valid
    private final List<FrilansOppdrag> frilansOppdrag;
    private final List<String> vedlegg;

    public Frilans(ÅpenPeriode periode,
            boolean harInntektFraFosterhjem,
            boolean nyOppstartet,
            List<FrilansOppdrag> frilansOppdrag,
            List<String> vedlegg) {
        this.periode = periode;
        this.harInntektFraFosterhjem = harInntektFraFosterhjem;
        this.nyOppstartet = nyOppstartet;
        this.frilansOppdrag = Optional.ofNullable(frilansOppdrag).orElse(emptyList());
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
    }
}
