package no.nav.foreldrepenger.mottak.innsending.pdf;

import java.io.IOException;
import java.util.function.Function;

@FunctionalInterface
public interface PdfThrowableFunction<T, R, E extends Exception> {
    R apply(T t) throws IOException;

    static <T, R, E extends Exception> Function<T, R> uncheck(PdfThrowableFunction<T, R, E> fn) {
        return t -> {
            try {
                return fn.apply(t);
            } catch (IOException e) {
                throw new RuntimeException("COS-feil", e);
            }
        };
    }

}
