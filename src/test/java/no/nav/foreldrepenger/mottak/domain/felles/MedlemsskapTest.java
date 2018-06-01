package no.nav.foreldrepenger.mottak.domain.felles;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ArbeidsInformasjon;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MedlemsskapTest {

    @Test
    public void withinPeriod() {
        Medlemsskap medlemsskap = medlemsskap();
        assertTrue(medlemsskap.varUtenlands(LocalDate.of(2018, 1, 20)));
    }

    @Test
    public void firstDayOfPeriod() {
        Medlemsskap medlemsskap = medlemsskap();
        assertTrue(medlemsskap.varUtenlands(LocalDate.of(2018, 1, 12)));
    }

    @Test
    public void lastDayOfPeriod() {
        Medlemsskap medlemsskap = medlemsskap();
        assertTrue(medlemsskap.varUtenlands(LocalDate.of(2018, 2, 23)));
    }

    @Test
    public void beforePeriod() {
        Medlemsskap medlemsskap = medlemsskap();
        assertFalse(medlemsskap.varUtenlands(LocalDate.of(2018, 1, 11)));
    }

    @Test
    public void afterPeriod() {
        Medlemsskap medlemsskap = medlemsskap();
        assertFalse(medlemsskap.varUtenlands(LocalDate.of(2018, 2, 24)));
    }

    private Medlemsskap medlemsskap() {
        List<Utenlandsopphold> utenlandsopphold = Arrays.asList(
            new Utenlandsopphold(CountryCode.FI, new LukketPeriode(LocalDate.of(2018, 1, 12), LocalDate.of(2018, 2, 23)))
        );
        TidligereOppholdsInformasjon tidligere =
            new TidligereOppholdsInformasjon(false, ArbeidsInformasjon.IKKE_ARBEIDET, utenlandsopphold);
        FramtidigOppholdsInformasjon framtidige = new FramtidigOppholdsInformasjon(false, false, Collections.emptyList());
        return new Medlemsskap(tidligere, framtidige);
    }

}
