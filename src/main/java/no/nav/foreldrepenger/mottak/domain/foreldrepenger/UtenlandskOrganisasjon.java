package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public class UtenlandskOrganisasjon extends EgenNæring {

    private String orgName;
    private CountryCode registrertLand;
    @Positive
    @Max(100)
    @Min(0)
    private int stillingsprosent;

    @Builder
    private UtenlandskOrganisasjon(CountryCode arbeidsland, Virksomhetstype virksomhetsType, ÅpenPeriode periode,
            String beskrivelseRelasjon, Regnskapsfører regnskapsfører, boolean erNyOpprettet, boolean erVarigEndring,
            long næringsinntektBrutto, LocalDate endringsDato, String beskrivelseEndring, List<String> vedlegg,
            String orgName, CountryCode registrertLand, int stillingsprosent) {
        super(arbeidsland, virksomhetsType, periode, beskrivelseRelasjon, regnskapsfører, erNyOpprettet, erVarigEndring,
                næringsinntektBrutto, endringsDato, beskrivelseEndring, vedlegg);
        this.orgName = orgName;
        this.registrertLand = registrertLand;
        this.stillingsprosent = stillingsprosent;
    }

}
