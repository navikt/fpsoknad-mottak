package no.nav.foreldrepenger.mottak.domain.felles;

import static com.neovisionaries.i18n.CountryCode.FI;
import static com.neovisionaries.i18n.CountryCode.NO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.ArbeidsInformasjon;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.Utenlandsopphold;

class UtenlandsoppholdTest {

    private static final LocalDate FROM_PAST = LocalDate.now().minusMonths(6);
    private static final LocalDate TO_PAST = LocalDate.now().minusMonths(1);
    private static final LocalDate FROM_FUTURE = LocalDate.now().plusMonths(1);
    private static final LocalDate TO_FUTURE = LocalDate.now().plusMonths(6);

    @Test
    void withinPeriod() {
        var ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(LocalDate.now().minusMonths(4)));
    }

    @Test
    void withinPeriodFramtid() {
        var ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(LocalDate.now().plusMonths(4)));
    }

    @Test
    void firstDayOfFuturePeriod() {
        var ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(FROM_FUTURE));
    }

    @Test
    void lastDayOfPastPeriod() {
        var ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(TO_PAST));
    }

    @Test
    void firstDayOfPastPeriod() {
        var ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(FROM_PAST));
    }

    @Test
    void lastDayOfFuturePeriod() {
        var ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(TO_FUTURE));
    }

    @Test
    void beforePeriodFortid() {
        var ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(NO, ms.landVedDato(LocalDate.now().minusMonths(7)));
    }

    @Test
    void beforePeriodFramtid() {
        var ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(NO, ms.landVedDato(LocalDate.now().plusDays(1)));
    }

    @Test
    void afterPeriodFramtid() {
        var ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(NO, ms.landVedDato(LocalDate.now().plusMonths(7)));
    }

    private static TidligereOppholdsInformasjon tidligereOpphold() {
        var utenlandsopphold = List.of(
                new Utenlandsopphold(FI, new LukketPeriode(FROM_PAST, TO_PAST)));
        return new TidligereOppholdsInformasjon(ArbeidsInformasjon.IKKE_ARBEIDET, utenlandsopphold);
    }

    private static FramtidigOppholdsInformasjon framtidigOpphold() {
        var utenlandsopphold = List.of(
                new Utenlandsopphold(FI, new LukketPeriode(FROM_FUTURE, TO_FUTURE)));
        return new FramtidigOppholdsInformasjon(utenlandsopphold);
    }

}
