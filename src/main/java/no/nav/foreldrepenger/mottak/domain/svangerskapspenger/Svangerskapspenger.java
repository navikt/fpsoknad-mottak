package no.nav.foreldrepenger.mottak.domain.svangerskapspenger;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.foreldrepenger.mottak.domain.Ytelse;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class Svangerskapspenger extends Ytelse {

    @NotNull
    private final LocalDate termindato;
    @Nullable
    private final LocalDate fødselsdato;
    @Valid
    private final Medlemsskap medlemsskap;
    @Valid
    private final Opptjening opptjening;
    private final List<Tilrettelegging> tilrettelegging;

    @JsonCreator
    public Svangerskapspenger(@JsonProperty("termindato") LocalDate termindato,
            @JsonProperty("fødselsdato") LocalDate fødselsdato,
            @JsonProperty("medlemsskap") Medlemsskap medlemsskap,
            @JsonProperty("opptjening") Opptjening opptjening,
            @JsonProperty("tilrettelegging") List<Tilrettelegging> tilrettelegging) {
        this.termindato = termindato;
        this.fødselsdato = fødselsdato;
        this.medlemsskap = medlemsskap;
        this.opptjening = opptjening;
        this.tilrettelegging = tilrettelegging;
    }

}
