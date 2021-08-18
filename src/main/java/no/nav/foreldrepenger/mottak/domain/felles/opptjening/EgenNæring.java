package no.nav.foreldrepenger.mottak.domain.felles.opptjening;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.felles.ÅpenPeriode;

@Data
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = NorskOrganisasjon.class, name = "norsk"),
        @Type(value = UtenlandskOrganisasjon.class, name = "utenlandsk")
})

@ToString(exclude = "vedlegg")
@EqualsAndHashCode(exclude = "vedlegg")
public abstract sealed class EgenNæring permits NorskOrganisasjon,UtenlandskOrganisasjon {

    private final List<Virksomhetstype> virksomhetsTyper;
    private final ÅpenPeriode periode;
    private final boolean nærRelasjon;
    @Valid
    private final List<Regnskapsfører> regnskapsførere;
    private final boolean erNyOpprettet;
    private final boolean erVarigEndring;
    private final boolean erNyIArbeidslivet;
    private final long næringsinntektBrutto;
    private final LocalDate endringsDato;
    private final LocalDate oppstartsDato;
    @Length(max = 1000)
    private final String beskrivelseEndring;
    private final ProsentAndel stillingsprosent;
    private final List<String> vedlegg;

    @JsonCreator
    public EgenNæring(
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
            @JsonProperty("stillingsprosent") ProsentAndel stillingsprosent,
            @JsonProperty("vedlegg") List<String> vedlegg) {
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
