package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.validation.Orgnr;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NorskOrganisasjon extends EgenNæring {

    @Orgnr
    private final String orgNummer;
    @Length(max = 50)
    private final String orgName;

    @Builder
    private NorskOrganisasjon(CountryCode arbeidsland, List<Virksomhetstype> virksomhetsTyper, ÅpenPeriode periode,
            boolean nærRelasjon, List<Regnskapsfører> regnskapsførere, boolean erNyOpprettet, boolean erVarigEndring,
            boolean erNyIArbeidslivet, long næringsinntektBrutto, LocalDate endringsDato, String beskrivelseEndring,
            List<String> vedlegg,
            String orgNummer, String orgName) {
        super(arbeidsland, virksomhetsTyper, periode, nærRelasjon, regnskapsførere, erNyOpprettet,
                erVarigEndring, erNyIArbeidslivet,
                næringsinntektBrutto, endringsDato, beskrivelseEndring, vedlegg);
        this.orgName = orgName;
        this.orgNummer = orgNummer;
    }
}
