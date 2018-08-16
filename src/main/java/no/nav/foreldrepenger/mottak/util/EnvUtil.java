package no.nav.foreldrepenger.mottak.util;

import org.springframework.core.env.Environment;

public final class EnvUtil {

    private EnvUtil() {

    }

    public static boolean isDevOrPreprod(Environment env) {
        return env == null || env.acceptsProfiles("dev", "preprod");
    }

    public static boolean isProd(Environment env) {
        return !isDevOrPreprod(env);
    }
}
