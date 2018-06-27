package no.nav.foreldrepenger.mottak.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.MDC;

public class TestMDCUtil {

    private static final String MEANING_OF_LIFE = "42";
    private static final String CALL_ID = "callId";

    @Test
    public void testPutGet() {
        String key = "Nav-callId";
        MDCUtil.put(key, MEANING_OF_LIFE);
        assertEquals(MEANING_OF_LIFE, MDC.get(CALL_ID));
        Pair<String, String> flightValues = MDCUtil.getForTransfer(CALL_ID);
        assertEquals(MEANING_OF_LIFE, flightValues.getSecond());
        assertEquals(key, flightValues.getFirst());
    }
}
