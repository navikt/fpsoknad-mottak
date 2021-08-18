package no.nav.foreldrepenger.mottak.domain.felles.opptjening;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.felles.ÅpenPeriode;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public final class UtenlandskOrganisasjon extends EgenNæring {

    @Length(max = 100)
    private String orgName;
    @JsonAlias("arbeidsland")
    @NotNull
    private final CountryCode registrertILand;

    @Builder
    private UtenlandskOrganisasjon(
            CountryCode registrertILand,
            List<Virksomhetstype> virksomhetsTyper, ÅpenPeriode periode,
            boolean nærRelasjon, List<Regnskapsfører> regnskapsførere, boolean erNyOpprettet, boolean erVarigEndring,
            boolean erNyIArbeidslivet, long næringsinntektBrutto, LocalDate endringsDato, LocalDate oppstartsDato,
            String beskrivelseEndring, ProsentAndel stillingsprosent, 
            List<String> vedlegg, String orgName) {
        super(virksomhetsTyper, periode, nærRelasjon, regnskapsførere, erNyOpprettet,
                erVarigEndring, erNyIArbeidslivet,
                næringsinntektBrutto, endringsDato, oppstartsDato, beskrivelseEndring, stillingsprosent, vedlegg);
        this.orgName = orgName;
        this.registrertILand = registrertILand;
    }

}
