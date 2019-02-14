package no.nav.foreldrepenger.mottak.domain.felles;

import static com.neovisionaries.i18n.CountryCode.FI;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

public class UtenlandsoppholdTest {

    @Test
    public void withinPeriod() {
        TidligereOppholdsInformasjon opphold = tidligereOpphold();
        assertTrue(opphold.varUtenlands(LocalDate.of(2018, 1, 20)));
    }

    @Test
    public void withinPeriodFramtid() {
        FramtidigOppholdsInformasjon opphold = framtidigOpphold();
        assertTrue(opphold.skalVæreUtenlands(LocalDate.now().plusMonths(2)));
    }

    @Test
    public void firstDayOfPeriod() {
        TidligereOppholdsInformasjon opphold = tidligereOpphold();
        assertTrue(opphold.varUtenlands(LocalDate.of(2018, 1, 12)));
    }

    @Test
    public void lastDayOfPeriod() {
        TidligereOppholdsInformasjon opphold = tidligereOpphold();
        assertTrue(opphold.varUtenlands(LocalDate.of(2018, 2, 23)));
    }

    @Test
    public void beforePeriod() {
        TidligereOppholdsInformasjon opphold = tidligereOpphold();
        assertFalse(opphold.varUtenlands(LocalDate.of(2018, 1, 11)));
    }

    @Test
    public void beforePeriodFramtid() {
        FramtidigOppholdsInformasjon opphold = framtidigOpphold();
        assertFalse(opphold.skalVæreUtenlands(LocalDate.now().plusDays(1)));
    }

    @Test
    public void afterPeriod() {
        TidligereOppholdsInformasjon opphold = tidligereOpphold();
        assertFalse(opphold.varUtenlands(LocalDate.of(2018, 2, 24)));
    }

    @Test
    public void afterPeriodFramtid() {
        FramtidigOppholdsInformasjon opphold = framtidigOpphold();
        assertFalse(opphold.skalVæreUtenlands(LocalDate.now().plusMonths(5)));
    }

    private static TidligereOppholdsInformasjon tidligereOpphold() {
        List<Utenlandsopphold> utenlandsopphold = asList(
                new Utenlandsopphold(FI, new LukketPeriode(LocalDate.of(2018, 1, 12), LocalDate.of(2018, 2, 23))));
        return new TidligereOppholdsInformasjon(true, ArbeidsInformasjon.IKKE_ARBEIDET, utenlandsopphold);
    }

    private static FramtidigOppholdsInformasjon framtidigOpphold() {
        List<Utenlandsopphold> utenlandsopphold = asList(
                new Utenlandsopphold(FI,
                        new LukketPeriode(LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(4))));
        return new FramtidigOppholdsInformasjon(true, true, utenlandsopphold);
    }

}
