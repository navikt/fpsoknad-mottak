package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UtenlandskArbeidsforhold extends Arbeidsforhold {

    private final CountryCode land;
    private final boolean harHattArbeidIPerioden;

    @Builder
    private UtenlandskArbeidsforhold(String arbeidsgiverNavn, String bekreftelseRelasjon, ÅpenPeriode periode,
            List<String> vedlegg, CountryCode land, boolean harHattArbeidIPerioden) {
        super(arbeidsgiverNavn, bekreftelseRelasjon, periode, vedlegg);
        this.land = land;
        this.harHattArbeidIPerioden = harHattArbeidIPerioden;
    }

}
