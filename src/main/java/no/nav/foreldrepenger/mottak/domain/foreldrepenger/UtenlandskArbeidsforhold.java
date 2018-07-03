package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;

@Data
public class UtenlandskArbeidsforhold {

    private final CountryCode land;
    private final boolean harHattArbeidIPerioden;
    @Length(max = 50)
    private final String arbeidsgiverNavn;
    private final boolean nærRelasjon;
    private final ÅpenPeriode periode;
    private final List<String> vedlegg;

    @Builder
    @JsonCreator
    public UtenlandskArbeidsforhold(@JsonProperty("arbeidsgiverNavn") String arbeidsgiverNavn,
            @JsonProperty("nærRelasjon") boolean nærRelasjon,
            @JsonProperty("periode") ÅpenPeriode periode,
            @JsonProperty("vedlegg") List<String> vedlegg,
            @JsonProperty("land") CountryCode land,
            @JsonProperty("harHattArbeidIPerioden") boolean harHattArbeidIPerioden) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
        this.nærRelasjon = nærRelasjon;
        this.periode = periode;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
        this.land = land;
        this.harHattArbeidIPerioden = harHattArbeidIPerioden;
    }

}
