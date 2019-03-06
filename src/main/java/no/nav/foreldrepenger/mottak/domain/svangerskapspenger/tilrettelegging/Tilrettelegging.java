package no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;

import javax.validation.Valid;

import java.time.LocalDate;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@Data
@Valid
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = HelTilrettelegging.class, name = "hel"),
    @JsonSubTypes.Type(value = DelvisTilrettelegging.class, name = "delvis"),
    @JsonSubTypes.Type(value = IngenTilrettelegging.class, name = "ingen")
})
public abstract class Tilrettelegging {

    private Arbeidsforhold arbeidsforhold;
    private LocalDate behovForTilretteleggingFom;
    private final List<String> vedlegg;

    public Tilrettelegging(Arbeidsforhold arbeidsforhold, LocalDate behovForTilretteleggingFom, List<String> vedlegg) {
        this.arbeidsforhold = arbeidsforhold;
        this.behovForTilretteleggingFom = behovForTilretteleggingFom;
        this.vedlegg = vedlegg;
    }
}
