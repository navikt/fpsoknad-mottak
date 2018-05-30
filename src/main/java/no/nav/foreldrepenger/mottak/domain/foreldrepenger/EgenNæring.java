package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import java.time.LocalDate;
import java.util.List;

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

}
