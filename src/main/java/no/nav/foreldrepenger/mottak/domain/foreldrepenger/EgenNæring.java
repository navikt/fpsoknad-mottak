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
    private final Virksomhetstype virksomhetsType;
    private final ÅpenPeriode periode;
    private final String beskrivelseRelasjon;
    private final Regnskapsfører regnskapsfører;
    private final boolean erNyOpprettet;
    private final boolean erVarigEndring;
    private final long næringsinntektBrutto;
    private final LocalDate endringsDato;
    private final String beskrivelseEndring;
    private final List<String> vedlegg;

    @JsonCreator
    public EgenNæring(@JsonProperty("arbeidsland") CountryCode arbeidsland,
            @JsonProperty("virksomhetsType") Virksomhetstype virksomhetsType,
            @JsonProperty("periode") ÅpenPeriode periode,
            @JsonProperty("beskrivelseRelasjon") String beskrivelseRelasjon,
            @JsonProperty("regnskapsfører") Regnskapsfører regnskapsfører,
            @JsonProperty("erNyOpprettet") boolean erNyOpprettet,
            @JsonProperty("erVarigEndring") boolean erVarigEndring,
            @JsonProperty("næringsinntektBrutto") long næringsinntektBrutto,
            @JsonProperty("endringsDato") LocalDate endringsDato,
            @JsonProperty("beskrivelseEndring") String beskrivelseEndring,
            @JsonProperty("vedlegg") List<String> vedlegg) {
        this.arbeidsland = arbeidsland;
        this.virksomhetsType = virksomhetsType;
        this.periode = periode;
        this.beskrivelseRelasjon = beskrivelseRelasjon;
        this.regnskapsfører = regnskapsfører;
        this.erNyOpprettet = erNyOpprettet;
        this.erVarigEndring = erVarigEndring;
        this.næringsinntektBrutto = næringsinntektBrutto;
        this.endringsDato = endringsDato;
        this.beskrivelseEndring = beskrivelseEndring;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
    }
}
