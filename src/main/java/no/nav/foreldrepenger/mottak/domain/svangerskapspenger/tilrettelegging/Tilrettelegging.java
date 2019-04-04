package no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;

@Data
@EqualsAndHashCode(exclude = { "vedlegg" })
@ToString(exclude = { "vedlegg" })
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HelTilrettelegging.class, name = "hel"),
        @JsonSubTypes.Type(value = DelvisTilrettelegging.class, name = "delvis"),
        @JsonSubTypes.Type(value = IngenTilrettelegging.class, name = "ingen")
})

public abstract class Tilrettelegging {

    private final Arbeidsforhold arbeidsforhold;
    private final LocalDate behovForTilretteleggingFom;
    private final List<String> vedlegg;

    public Tilrettelegging(Arbeidsforhold arbeidsforhold, LocalDate behovForTilretteleggingFom, List<String> vedlegg) {
        this.arbeidsforhold = arbeidsforhold;
        this.behovForTilretteleggingFom = behovForTilretteleggingFom;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
    }
}
