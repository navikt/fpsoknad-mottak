package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UtsettelsesPeriode extends LukketPeriodeMedVedlegg {

    private final UtsettelsesÅrsak årsak;

    public UtsettelsesPeriode(LocalDate fom, LocalDate tom, List<String> vedlegg, UtsettelsesÅrsak årsak) {
        super(fom, tom, vedlegg == null ? Collections.emptyList() : vedlegg);
        this.årsak = årsak;
    }
}
