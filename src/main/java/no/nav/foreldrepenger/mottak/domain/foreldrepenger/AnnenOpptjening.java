package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = "vedlegg")
public class AnnenOpptjening {

    private final AnnenOpptjeningType type;
    private final ÅpenPeriode periode;
    private final List<String> vedlegg;

    @JsonCreator
    public AnnenOpptjening(AnnenOpptjeningType type, ÅpenPeriode periode, List<String> vedlegg) {
        this.type = type;
        this.periode = periode;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
    }
}
