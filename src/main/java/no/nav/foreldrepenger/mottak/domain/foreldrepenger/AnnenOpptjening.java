package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;

@Data
public class AnnenOpptjening {

    private final AnnenOpptjeningType type;
    private final ÅpenPeriode periode;
    private final List<String> vedlegg;

    @JsonCreator
    public AnnenOpptjening(AnnenOpptjeningType type, ÅpenPeriode periode, List<String> vedlegg) {
        this.type = type;
        this.periode = periode;
        this.vedlegg = (vedlegg == null ? Collections.emptyList() : vedlegg);
    }

}
