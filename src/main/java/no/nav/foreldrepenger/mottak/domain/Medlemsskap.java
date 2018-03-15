package no.nav.foreldrepenger.mottak.domain;

import javax.validation.Valid;

import lombok.Data;

@Data
public class Medlemsskap {

    @Valid
    private final TidligereOppholdsInformasjon tidligereOppholdsInfo;
    @Valid
    private final FramtidigOppholdsInformasjon framtidigOppholdsInfo;

}
