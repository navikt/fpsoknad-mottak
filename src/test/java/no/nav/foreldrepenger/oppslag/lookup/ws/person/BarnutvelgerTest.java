package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
public class BarnutvelgerTest {

    private static final Fodselsnummer MOR = new Fodselsnummer("28016432662");
    private static final String BARN_FNR = DateTimeFormatter.ofPattern("ddMMyy").format(now().minusMonths(2)) + "36325";
    
    @Test
    public void testStønadsberettigetRelasjon() {
        int months = 2;

        Barn barn = new Barn(MOR, new Fodselsnummer(BARN_FNR), now().minusMonths(1));
        assertThat(new BarnMorRelasjonSjekkendeBarnutvelger(months).erStonadsberettigetBarn(MOR, barn)).isTrue();

        barn = new Barn(MOR, new Fodselsnummer(BARN_FNR), now().minusMonths(3));
        assertThat(new BarnMorRelasjonSjekkendeBarnutvelger(months).erStonadsberettigetBarn(MOR, barn)).isFalse();
    }

}
