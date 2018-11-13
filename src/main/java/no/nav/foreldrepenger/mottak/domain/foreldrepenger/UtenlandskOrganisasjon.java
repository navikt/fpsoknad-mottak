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
    private final CountryCode registrertILand;

    @Builder
    private UtenlandskOrganisasjon(CountryCode registrertILand,
            List<Virksomhetstype> virksomhetsTyper, ÅpenPeriode periode,
            boolean nærRelasjon, List<Regnskapsfører> regnskapsførere, boolean erNyOpprettet, boolean erVarigEndring,
            boolean erNyIArbeidslivet, long næringsinntektBrutto, LocalDate endringsDato, LocalDate oppstartsDato,
            String beskrivelseEndring, Double stillingsprosent,
            List<String> vedlegg, String orgName) {
        super(virksomhetsTyper, periode, nærRelasjon, regnskapsførere, erNyOpprettet,
                erVarigEndring, erNyIArbeidslivet,
                næringsinntektBrutto, endringsDato, oppstartsDato, beskrivelseEndring, stillingsprosent, vedlegg);
        this.orgName = orgName;
        this.registrertILand = registrertILand;
    }

}
