package no.nav.foreldrepenger.mottak.domain;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.foreldrepenger.mottak.domain.felles.PåkrevdVedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

@Data
@Validated
@EqualsAndHashCode(exclude = "mottattdato")
@JsonPropertyOrder({ "mottattdato", "søker", "ytelse", "begrunnelseForSenSøknad", "tilleggsopplysninger", "vedlegg" })
public class Søknad {
    @NotNull
    private final LocalDateTime mottattdato;
    @Valid
    private final Søker søker;
    @Valid
    private final Ytelse ytelse;
    @Length(max = 2000)
    private String begrunnelseForSenSøknad;
    @Length(max = 4000)
    private String tilleggsopplysninger;
    private final List<Vedlegg> vedlegg;

    public Søknad(LocalDateTime mottattdato, Søker søker, Ytelse ytelse, Vedlegg... vedlegg) {
        this(mottattdato, søker, ytelse, asList(vedlegg));
    }

    @JsonCreator
    public Søknad(@JsonProperty("mottattdato") LocalDateTime mottattdato, @JsonProperty("søker") Søker søker,
            @JsonProperty("ytelse") Ytelse ytelse,
            @JsonProperty("vedlegg") List<Vedlegg> vedlegg) {
        this.mottattdato = mottattdato;
        this.søker = søker;
        this.ytelse = ytelse;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
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
