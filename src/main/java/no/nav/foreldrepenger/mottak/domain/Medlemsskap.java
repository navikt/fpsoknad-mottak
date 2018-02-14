package no.nav.foreldrepenger.mottak.domain;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.serialization.MedlemsskapDeserializer;
import no.nav.foreldrepenger.mottak.domain.serialization.MedlemsskapSerializer;

@Data
@JsonSerialize(using = MedlemsskapSerializer.class)
@JsonDeserialize(using = MedlemsskapDeserializer.class)
public class Medlemsskap {

    @Valid
    private final TidligereOppholdsInformasjon tidligereOppholdsInfo;
    @Valid
    private final FramtidigOppholdsInformasjon framtidigOppholdsInfo;

}
