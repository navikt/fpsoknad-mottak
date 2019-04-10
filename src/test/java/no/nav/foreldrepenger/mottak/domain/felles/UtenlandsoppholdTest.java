package no.nav.foreldrepenger.mottak.domain.felles;

import static com.neovisionaries.i18n.CountryCode.FI;
import static com.neovisionaries.i18n.CountryCode.NO;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;

public class UtenlandsoppholdTest {

    private static final LocalDate FROM_PAST = LocalDate.now().minusMonths(6);
    private static final LocalDate TO_PAST = LocalDate.now().minusMonths(1);
    private static final LocalDate FROM_FUTURE = LocalDate.now().plusMonths(1);
    private static final LocalDate TO_FUTURE = LocalDate.now().plusMonths(6);

    @Test
    public void withinPeriod() {
        Medlemsskap ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(LocalDate.now().minusMonths(4)));
    }

    @Test
    public void withinPeriodFramtid() {
        Medlemsskap ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(LocalDate.now().plusMonths(4)));
    }

    @Test
    public void firstDayOfFuturePeriod() {
        Medlemsskap ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(FROM_FUTURE));
    }

    @Test
    public void lastDayOfPastPeriod() {
        Medlemsskap ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(TO_PAST));
    }

    @Test
    public void firstDayOfPastPeriod() {
        Medlemsskap ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(FROM_PAST));
    }

    @Test
    public void lastDayOfFuturePeriod() {
        Medlemsskap ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(FI, ms.landVedDato(TO_FUTURE));
    }

    @Test
    public void beforePeriodFortid() {
        Medlemsskap ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(NO, ms.landVedDato(LocalDate.now().minusMonths(7)));
    }

    @Test
    public void beforePeriodFramtid() {
        Medlemsskap ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(NO, ms.landVedDato(LocalDate.now().plusDays(1)));
    }

    @Test
    public void afterPeriodFramtid() {
        Medlemsskap ms = new Medlemsskap(tidligereOpphold(), framtidigOpphold());
        assertEquals(NO, ms.landVedDato(LocalDate.now().plusMonths(7)));
    }

    private static TidligereOppholdsInformasjon tidligereOpphold() {
        List<Utenlandsopphold> utenlandsopphold = asList(
                new Utenlandsopphold(FI, new LukketPeriode(FROM_PAST, TO_PAST)));
        return new TidligereOppholdsInformasjon(ArbeidsInformasjon.IKKE_ARBEIDET, utenlandsopphold);
    }

    private static FramtidigOppholdsInformasjon framtidigOpphold() {
        List<Utenlandsopphold> utenlandsopphold = asList(
                new Utenlandsopphold(FI, new LukketPeriode(FROM_FUTURE, TO_FUTURE)));
        return new FramtidigOppholdsInformasjon(utenlandsopphold);
    }

}
