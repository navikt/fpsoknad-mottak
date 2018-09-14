package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.*;

public class ArbeidsforholdClientWsTest {

    LocalDate yesterday = LocalDate.now().minusDays(1);
    LocalDate oneWeekAgo = LocalDate.now().minusDays(7);
    LocalDate tomorrow = LocalDate.now().plusDays(2);

    @Test
    public void noEndDateSet() {
        ArbeidsforholdClientWs lookup = new ArbeidsforholdClientWs(null, null, null);
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold("S. Vindel & sønn", "snekker",
            100.0, yesterday, Optional.empty());
        boolean ongoing = lookup.isOngoing(arbeidsforhold);
        assertTrue(ongoing);
    }

    @Test
    public void endDateInThePast() {
        ArbeidsforholdClientWs lookup = new ArbeidsforholdClientWs(null, null, null);
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold("S. Vindel & sønn", "snekker",
            100.0, oneWeekAgo, Optional.ofNullable(yesterday));
        boolean ongoing = lookup.isOngoing(arbeidsforhold);
        assertFalse(ongoing);
    }

    @Test
    public void endDateInTheFuture() {
        ArbeidsforholdClientWs lookup = new ArbeidsforholdClientWs(null, null, null);
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold("S. Vindel & sønn", "snekker",
            100.0, oneWeekAgo, Optional.ofNullable(tomorrow));
        boolean ongoing = lookup.isOngoing(arbeidsforhold);
        assertTrue(ongoing);
    }

}
