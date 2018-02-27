package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Søknad {
    @NotNull
    private final LocalDateTime mottattdato;
    @Valid
    private final Søker søker;
    @Valid
    private final Ytelse ytelse;
    private String begrunnelseForSenSøknad;
    private String tilleggsopplysninger;
    private final List<PåkrevdVedlegg> påkrevdeVedlegg;
    private final List<ValgfrittVedlegg> frivilligeVedlegg;

    public Søknad(LocalDateTime mottattdato, Søker søker, Ytelse ytelse, Vedlegg... vedlegg) {
        this(mottattdato, søker, ytelse, Arrays.asList(vedlegg));
    }

    public Søknad(LocalDateTime mottattdato, Søker søker, Ytelse ytelse, List<Vedlegg> vedlegg) {
        this(mottattdato, søker, ytelse, påkrevde(vedlegg), valgfrie(vedlegg));
    }

    @JsonCreator
    public Søknad(@JsonProperty("mottattdato") LocalDateTime mottattdato, @JsonProperty("søker") Søker søker,
                  @JsonProperty("ytelse") Ytelse ytelse,
                  @JsonProperty("påkrevdeVedlegg") List<PåkrevdVedlegg> påkrevdeVedlegg,
                  @JsonProperty("frivilligeVedlegg") List<ValgfrittVedlegg> frivilligeVedlegg) {
        this.mottattdato = mottattdato;
        this.søker = søker;
        this.ytelse = ytelse;
        this.påkrevdeVedlegg = påkrevdeVedlegg == null ? Collections.emptyList() : påkrevdeVedlegg;
        this.frivilligeVedlegg = frivilligeVedlegg == null ? Collections.emptyList() : frivilligeVedlegg;
    }

    private static List<PåkrevdVedlegg> påkrevde(List<Vedlegg> vedlegg) {
        return vedlegg.stream()
                .filter(s -> s instanceof PåkrevdVedlegg)
                .map(s -> PåkrevdVedlegg.class.cast(s))
                .collect(Collectors.toList());
    }

    private static List<ValgfrittVedlegg> valgfrie(List<Vedlegg> vedlegg) {
        return vedlegg.stream()
                .filter(s -> s instanceof ValgfrittVedlegg)
                .map(s -> ValgfrittVedlegg.class.cast(s))
                .collect(Collectors.toList());
    }
}
