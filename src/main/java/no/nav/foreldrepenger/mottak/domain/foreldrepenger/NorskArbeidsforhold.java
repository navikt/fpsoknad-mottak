package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NorskArbeidsforhold extends Arbeidsforhold {
    private final String orgNummer;
    private final ArbeidsforholdType type;

    @Builder
    private NorskArbeidsforhold(String arbeidsgiverNavn, String bekreftelseRelasjon, ÅpenPeriode periode,
            List<String> vedlegg, String orgNummer, ArbeidsforholdType type) {
        super(arbeidsgiverNavn, bekreftelseRelasjon, periode, vedlegg == null ? Collections.emptyList() : vedlegg);
        this.orgNummer = orgNummer;
        this.type = type;
    }
}
