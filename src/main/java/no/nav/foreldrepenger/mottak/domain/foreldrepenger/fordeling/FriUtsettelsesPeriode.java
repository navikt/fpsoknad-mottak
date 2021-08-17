package no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FriUtsettelsesPeriode extends UtsettelsesPeriode {

    public FriUtsettelsesPeriode(LocalDate fom, LocalDate tom, boolean erArbeidstaker,
            @NotNull UtsettelsesÅrsak årsak, MorsAktivitet morsAktivitetsType) {
        super(fom, tom, erArbeidstaker, null, årsak, null, morsAktivitetsType, List.of());
    }

}
