package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;

@Data
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = NorskOrganisasjon.class, name = "norsk"),
        @Type(value = UtenlandskOrganisasjon.class, name = "utenlandsk")
})

public abstract class EgenNæring {

    private final CountryCode arbeidsland;
    private final List<Virksomhetstype> virksomhetsTyper;
    private final ÅpenPeriode periode;
    private final boolean nærRelasjon;
    private final List<Regnskapsfører> regnskapsførere;
    private final boolean erNyOpprettet;
    private final boolean erVarigEndring;
    private final boolean erNyIArbeidslivet;
    private final long næringsinntektBrutto;
    private final LocalDate endringsDato;
    private final LocalDate oppstartsDato;
    private final String beskrivelseEndring;
    private final Float stillingsprosent;
    private final List<String> vedlegg;

    @JsonCreator
    public EgenNæring(@JsonProperty("arbeidsland") CountryCode arbeidsland,
            @JsonProperty("virksomhetsType") List<Virksomhetstype> virksomhetsTyper,
            @JsonProperty("periode") ÅpenPeriode periode,
            @JsonProperty("nærRelasjon") boolean nærRelasjon,
            @JsonProperty("regnskapsførere") List<Regnskapsfører> regnskapsførere,
            @JsonProperty("erNyOpprettet") boolean erNyOpprettet,
            @JsonProperty("erVarigEndring") boolean erVarigEndring,
            @JsonProperty("erNyIArbeidslivet") boolean erNyIArbeidslivet,
            @JsonProperty("næringsinntektBrutto") long næringsinntektBrutto,
            @JsonProperty("endringsDato") LocalDate endringsDato,
            @JsonProperty("oppstartsDato") LocalDate oppstartsDato,
            @JsonProperty("beskrivelseEndring") String beskrivelseEndring,
            @JsonProperty("stillingsprosent") Float stillingsprosent,
            @JsonProperty("vedlegg") List<String> vedlegg) {
        this.arbeidsland = arbeidsland;
        this.virksomhetsTyper = virksomhetsTyper;
        this.periode = periode;
        this.nærRelasjon = nærRelasjon;
        this.regnskapsførere = regnskapsførere;
        this.erNyOpprettet = erNyOpprettet;
        this.erNyIArbeidslivet = erNyIArbeidslivet;
        this.erVarigEndring = erVarigEndring;
        this.næringsinntektBrutto = næringsinntektBrutto;
        this.endringsDato = endringsDato;
        this.oppstartsDato = oppstartsDato;
        this.beskrivelseEndring = beskrivelseEndring;
        this.stillingsprosent = stillingsprosent;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
    }
}
