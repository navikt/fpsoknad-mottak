package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public class UtenlandskOrganisasjon extends EgenNæring {

    @Length(max = 50)
    private String orgName;

    @Builder
    private UtenlandskOrganisasjon(CountryCode arbeidsland, List<Virksomhetstype> virksomhetsTyper, ÅpenPeriode periode,
            boolean nærRelasjon, List<Regnskapsfører> regnskapsførere, boolean erNyOpprettet, boolean erVarigEndring,
            long næringsinntektBrutto, LocalDate endringsDato, String beskrivelseEndring, List<String> vedlegg,
            String orgName) {
        super(arbeidsland, virksomhetsTyper, periode, nærRelasjon, regnskapsførere, erNyOpprettet,
                erVarigEndring,
                næringsinntektBrutto, endringsDato, beskrivelseEndring, vedlegg);
        this.orgName = orgName;
    }

}
