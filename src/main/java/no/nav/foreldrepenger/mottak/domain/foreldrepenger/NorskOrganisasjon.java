package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NorskOrganisasjon extends EgenNæring {

    private final String orgNummer;
    private final String orgName;

    @Builder
    private NorskOrganisasjon(CountryCode arbeidsland, Virksomhetstype virksomhetsType, ÅpenPeriode periode,
            String beskrivelseRelasjon, Regnskapsfører regnskapsfører, boolean erNyOpprettet, boolean erVarigEndring,
            long næringsinntektBrutto, LocalDate endringsDato, String beskrivelseEndring, List<Vedlegg> vedlegg,
            String orgNummer, String orgName) {
        super(arbeidsland, virksomhetsType, periode, beskrivelseRelasjon, regnskapsfører, erNyOpprettet, erVarigEndring,
                næringsinntektBrutto, endringsDato, beskrivelseEndring,
                vedlegg == null ? Collections.emptyList() : vedlegg);
        this.orgName = orgName;
        this.orgNummer = orgNummer;

    }

}
