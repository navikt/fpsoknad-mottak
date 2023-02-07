package no.nav.foreldrepenger.mottak.innsending;

import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.Ytelse;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;

final class SøknadValidator {

    static void validerFørstegangFpSøknad(Søknad søknad) {
        var ytelse = søknad.getYtelse();
        validerFpSøknad(ytelse);
        if (ytelse instanceof Foreldrepenger foreldrepenger) {
            var perioder = foreldrepenger.fordeling().perioder();
            //Allerede validert på minst en periode
            if (perioder.stream().allMatch(SøknadValidator::erFriUtsettelse)) {
                throw new UnexpectedInputException(
                    "Søknad må inneholde minst en søknadsperiode som ikke" + "er fri utsettelse");
            }
        }
    }

    static void validerFpSøknad(Ytelse ytelse) {
        if (ytelse instanceof Foreldrepenger foreldrepenger) {
            var perioder = foreldrepenger.fordeling().perioder();
            if (perioder.isEmpty()) {
                throw new UnexpectedInputException("Søknad må inneholde minst en søknadsperiode");
            }
            if (finnesOverlapp(perioder)) {
                throw new UnexpectedInputException("Søknad inneholder overlappende søknadsperioder");
            }
        }
    }

    private static boolean erFriUtsettelse(LukketPeriodeMedVedlegg p) {
        return p instanceof UtsettelsesPeriode utsettelsesPeriode && Objects.equals(utsettelsesPeriode.getÅrsak(),
            UtsettelsesÅrsak.FRI);
    }

    static boolean finnesOverlapp(List<LukketPeriodeMedVedlegg> perioder) {
        for (int i = 0; i < perioder.size() - 1; i++) {
            for (int j = i + 1; j < perioder.size(); j++) {
                if (overlapper(perioder.get(i), perioder.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean overlapper(LukketPeriodeMedVedlegg periode1, LukketPeriodeMedVedlegg periode2) {
        var fomBeforeOrEqual = periode1.getFom().isBefore(periode2.getTom()) || periode1.getFom().isEqual(periode2.getTom());
        var tomAfterOrEqual = periode1.getTom().isAfter(periode2.getFom()) || periode1.getTom().isEqual(periode2.getFom());
        return fomBeforeOrEqual && tomAfterOrEqual;
    }
}
