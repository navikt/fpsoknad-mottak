package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class Medlemsskap {

    private final List<Utenlandsopphold> utenlandsopphold;
    private final List<Varighet> norgesOpphold;
    private final OppholdsInformasjon oppholdsInfo;

    @ConstructorProperties({ "oppholdsInfo", "utenlandsopphold", "norgesOpphold" })
    @Builder
    public Medlemsskap(OppholdsInformasjon oppholdsInfo, List<Utenlandsopphold> utenlandsopphold,
            List<Varighet> norgesOopphold) {
        this.utenlandsopphold = utenlandsopphold;
        this.oppholdsInfo = oppholdsInfo;
        this.norgesOpphold = norgesOopphold;

    }

}
