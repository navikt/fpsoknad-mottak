package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import wiremock.com.fasterxml.jackson.annotation.JsonCreator;

@Data
public class Opptjening {

    private final List<UtenlandskArbeidsforhold> utenlandskArbeidsforhold;
    private final List<EgenNæring> egenNæring;
    private final List<AnnenOpptjening> annenOpptjening;
    private final Frilans frilans;

    @JsonCreator
    public Opptjening(@JsonProperty("arbeidsforhold") List<UtenlandskArbeidsforhold> utenlandskArbeidsforhold,
            @JsonProperty("egenNæring") List<EgenNæring> egenNæring,
            @JsonProperty("annenOpptjening") List<AnnenOpptjening> annenOpptjening,
            @JsonProperty("frilans") Frilans frilans) {
        this.utenlandskArbeidsforhold = Optional.ofNullable(utenlandskArbeidsforhold).orElse(emptyList());
        this.egenNæring = Optional.ofNullable(egenNæring).orElse(emptyList());
        this.annenOpptjening = Optional.ofNullable(annenOpptjening).orElse(emptyList());
        this.frilans = frilans;
    }
}
