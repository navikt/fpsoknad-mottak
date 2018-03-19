package no.nav.foreldrepenger.mottak.domain;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@Validated
@JsonPropertyOrder({ "mottattdato", "søker", "ytelse", "begrunnelseForSenSøknad", "tilleggsopplysninger", "vedlegg" })
public class Søknad {
    @NotNull
    private final LocalDateTime mottattdato;
    @Valid
    private final Søker søker;
    @Valid
    private final Ytelse ytelse;
    private String begrunnelseForSenSøknad;
    private String tilleggsopplysninger;
    private final List<Vedlegg> vedlegg;

    public Søknad(LocalDateTime mottattdato, Søker søker, Ytelse ytelse, Vedlegg... vedlegg) {
        this(mottattdato, søker, ytelse, Arrays.asList(vedlegg));
    }

    @JsonCreator
    public Søknad(@JsonProperty("mottattdato") LocalDateTime mottattdato, @JsonProperty("søker") Søker søker,
            @JsonProperty("ytelse") Ytelse ytelse,
            @JsonProperty("påkrevdeVedlegg") List<Vedlegg> vedlegg) {
        this.mottattdato = mottattdato;
        this.søker = søker;
        this.ytelse = ytelse;
        this.vedlegg = vedlegg;
    }

    @JsonIgnore
    public List<PåkrevdVedlegg> getPåkrevdeVedlegg() {
        return vedlegg.stream()
                .filter(s -> s instanceof PåkrevdVedlegg)
                .map(s -> PåkrevdVedlegg.class.cast(s))
                .collect(toList());
    }

    @JsonIgnore
    public List<ValgfrittVedlegg> getFrivilligeVedlegg() {
        return vedlegg.stream()
                .filter(s -> s instanceof ValgfrittVedlegg)
                .map(s -> ValgfrittVedlegg.class.cast(s))
                .collect(toList());
    }
}
