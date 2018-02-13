package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Søknad {
    @NotNull
    private final LocalDateTime motattdato;
    @Valid
    private final Søker søker;
    @Valid
    private final Ytelse ytelse;
    private String begrunnelseForSenSøknad;
    private String tilleggsopplysninger;
    private final List<PåkrevdVedlegg> påkrevdeVedlegg;
    private final List<ValgfrittVedlegg> frivilligeVedlegg;

    public Søknad(LocalDateTime motattdato, Søker søker, Ytelse ytelse) {
        this(motattdato, søker, ytelse, Collections.emptyList(), Collections.emptyList());
    }

    public Søknad(LocalDateTime motattdato, Søker søker, Ytelse ytelse, PåkrevdVedlegg vedlegg) {
        this(motattdato, søker, ytelse, Collections.singletonList(vedlegg), Collections.emptyList());
    }

    @JsonCreator
    public Søknad(@JsonProperty("motattdato") LocalDateTime motattdato, @JsonProperty("søker") Søker søker,
            @JsonProperty("ytelse") Ytelse ytelse,
            @JsonProperty("påkrevdeVedlegg") List<PåkrevdVedlegg> påkrevdeVedlegg,
            @JsonProperty("frivilligeVedlegg") List<ValgfrittVedlegg> frivilligeVedlegg) {
        this.motattdato = motattdato;
        this.søker = søker;
        this.ytelse = ytelse;
        this.påkrevdeVedlegg = påkrevdeVedlegg == null ? Collections.emptyList() : påkrevdeVedlegg;
        this.frivilligeVedlegg = frivilligeVedlegg == null ? Collections.emptyList() : frivilligeVedlegg;

    }
}
