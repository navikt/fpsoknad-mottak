package no.nav.foreldrepenger.mottak.domain.felles;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

import java.time.LocalDate;

@Data
@JsonPropertyOrder({ "tidligereOppholdsInfo", "framtidigOppholdsInfo" })
public class Medlemsskap {

    @Valid
    private final TidligereOppholdsInformasjon tidligereOppholdsInfo;
    @Valid
    private final FramtidigOppholdsInformasjon framtidigOppholdsInfo;

    public boolean varUtenlands(LocalDate day) {
        return tidligereOppholdsInfo.getUtenlandsOpphold().stream()
                .anyMatch(u -> isWithinPeriode(day, u));
    }

    private boolean isWithinPeriode(LocalDate day, Utenlandsopphold opphold) {
        LocalDate dayBeforeFirst = opphold.getVarighet().getFom().minusDays(1);
        LocalDate dayAfterLast = opphold.getVarighet().getTom().plusDays(1);
        return day.isAfter(dayBeforeFirst) && day.isBefore(dayAfterLast);
    }

}
