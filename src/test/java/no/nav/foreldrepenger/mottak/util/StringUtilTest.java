package no.nav.foreldrepenger.mottak.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringUtilTest {

    @Test
    public void testPadFnr() {
        assertEquals("111111*****", StringUtil.partialMask("11111111111"));
    }

    @Test
    public void testPadAllFnr() {
        assertEquals("***********", StringUtil.mask("11111111111"));
    }

    @Test
    public void testPadAllNull() {
        assertEquals("<null>", StringUtil.mask(null));
    }

    @Test
    public void testPadAllEmpty() {
        assertEquals("<null>", StringUtil.mask(""));
    }

}
