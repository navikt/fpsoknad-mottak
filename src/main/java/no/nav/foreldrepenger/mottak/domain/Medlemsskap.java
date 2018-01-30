package no.nav.foreldrepenger.mottak.domain;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Medlemsskap {

    @Valid
    private final TidligereOppholdsInformasjon tidligereOppholdsInfo;
    @Valid
    private final FramtidigOppholdsInformasjon fremtidigOppholdsInfo;

    @JsonCreator
    public Medlemsskap(@JsonProperty("tidligereOppholdsInfo") TidligereOppholdsInformasjon tidligereOppholdsInfo,
            @JsonProperty("fremtidigOppholdsInfo") FramtidigOppholdsInformasjon fremtidigOppholdsInfo) {
        this.tidligereOppholdsInfo = tidligereOppholdsInfo;
        this.fremtidigOppholdsInfo = fremtidigOppholdsInfo;
    }
}
