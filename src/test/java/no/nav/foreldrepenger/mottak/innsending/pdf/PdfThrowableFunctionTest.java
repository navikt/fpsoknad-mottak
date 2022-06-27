package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Function;

import static no.nav.foreldrepenger.mottak.innsending.pdf.PdfThrowableFunction.uncheck;
import static org.junit.jupiter.api.Assertions.*;

class PdfThrowableFunctionTest {

    @Test
    void gittIngenIOExceptionReturnererFunksjonenSomNormalt() {
        Function<String, String> fn = uncheck(s -> s);
        assertEquals("INPUT", fn.apply("INPUT"));
    }

    @Test
    void gittIOExceptionSkalWrappetFunksjonReturnereRuntimeException() {
        Function<String, String> fn = uncheck(s -> { throw new IOException(s); });
        assertThrows(RuntimeException.class,() -> fn.apply("INPUT"));
    }

}
