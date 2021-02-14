package no.nav.foreldrepenger.mottak.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StringUtilTest {

    @Test
    void testPadFnr() {
        assertEquals("111111*****", StringUtil.partialMask("11111111111"));
    }

    @Test
    void testPadAllFnr() {
        assertEquals("***********", StringUtil.mask("11111111111"));
    }

    @Test
    void testPadAllNull() {
        assertEquals("<null>", StringUtil.mask(null));
    }

    @Test
    void testPadAllEmpty() {
        assertEquals("<null>", StringUtil.mask(""));
    }

}
