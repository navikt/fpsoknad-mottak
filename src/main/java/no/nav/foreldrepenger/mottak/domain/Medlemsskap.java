package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Medlemsskap {

    @Valid
    private final List<Utenlandsopphold> utenlandsOpphold;
    @Valid
    private final List<LukketPeriode> norgesOpphold;
    private final OppholdsInformasjon oppholdsInfo;
    private final boolean boddINorgeSiste12;

    public Medlemsskap(OppholdsInformasjon oppholdsInfo) {
        this(oppholdsInfo, lastYear(), Collections.emptyList());

    }

    @JsonCreator
    public Medlemsskap(@JsonProperty("oppholdsInfo") OppholdsInformasjon oppholdsInfo,
            @JsonProperty("norgesOpphold") List<LukketPeriode> norgesOpphold,
            @JsonProperty("utenlandsOpphold") List<Utenlandsopphold> utenlandsOpphold) {
        this.utenlandsOpphold = utenlandsOpphold != null ? utenlandsOpphold : Collections.emptyList();
        this.oppholdsInfo = oppholdsInfo;
        this.norgesOpphold = norgesOpphold;
        this.boddINorgeSiste12 = !norgesOpphold.isEmpty();
    }

    private static List<LukketPeriode> lastYear() {
        return Collections.singletonList(new LukketPeriode(LocalDate.now().minus(Period.ofYears(1)), LocalDate.now()));
    }

}
