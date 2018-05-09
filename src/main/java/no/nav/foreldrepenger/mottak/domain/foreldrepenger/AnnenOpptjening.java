package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

@Data
public class AnnenOpptjening {

    private final AnnenOpptjeningType type;
    private final ÅpenPeriode periode;
    private final List<Vedlegg> vedlegg;

    @JsonCreator
    public AnnenOpptjening(AnnenOpptjeningType type, ÅpenPeriode periode, List<Vedlegg> vedlegg) {
        this.type = type;
        this.periode = periode;
        this.vedlegg = (vedlegg == null ? Collections.emptyList() : vedlegg);
    }

}
