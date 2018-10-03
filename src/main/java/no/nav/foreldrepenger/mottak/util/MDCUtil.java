package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.isProd;

import org.slf4j.MDC;
import org.springframework.core.env.Environment;

public final class MDCUtil {

    private MDCUtil() {

    }

    public static void putIfNotProd(Environment env, String key, String value) {
        if (!isProd(env)) {
            MDC.put(key, value);
        }
    }
}
