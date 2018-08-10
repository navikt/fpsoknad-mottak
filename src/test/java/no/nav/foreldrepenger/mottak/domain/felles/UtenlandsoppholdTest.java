package no.nav.foreldrepenger.mottak.domain.felles;

import static com.neovisionaries.i18n.CountryCode.FI;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

public class UtenlandsoppholdTest {

    @Test
    public void withinPeriod() {
        TidligereOppholdsInformasjon opphold = opphold();
        assertTrue(opphold.varUtenlands(LocalDate.of(2018, 1, 20)));
    }

    @Test
    public void firstDayOfPeriod() {
        TidligereOppholdsInformasjon opphold = opphold();
        assertTrue(opphold.varUtenlands(LocalDate.of(2018, 1, 12)));
    }

    @Test
    public void lastDayOfPeriod() {
        TidligereOppholdsInformasjon opphold = opphold();
        assertTrue(opphold.varUtenlands(LocalDate.of(2018, 2, 23)));
    }

    @Test
    public void beforePeriod() {
        TidligereOppholdsInformasjon opphold = opphold();
        assertFalse(opphold.varUtenlands(LocalDate.of(2018, 1, 11)));
    }

    @Test
    public void afterPeriod() {
        TidligereOppholdsInformasjon opphold = opphold();
        assertFalse(opphold.varUtenlands(LocalDate.of(2018, 2, 24)));
    }

    private static TidligereOppholdsInformasjon opphold() {
        List<Utenlandsopphold> utenlandsopphold = asList(
                new Utenlandsopphold(FI, new LukketPeriode(LocalDate.of(2018, 1, 12), LocalDate.of(2018, 2, 23))));
        return new TidligereOppholdsInformasjon(true, ArbeidsInformasjon.IKKE_ARBEIDET, utenlandsopphold);
    }

}
