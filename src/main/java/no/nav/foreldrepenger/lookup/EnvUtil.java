package no.nav.foreldrepenger.lookup;

import org.springframework.core.env.Environment;

public final class EnvUtil {

    public static final String PREPROD = "preprod";
    public static final String DEV = "dev";

    private EnvUtil() {

    }

    public static boolean isDevOrPreprod(Environment env) {
        return env == null || env.acceptsProfiles(DEV, PREPROD);
    }

    public static boolean isProd(Environment env) {
        return !isDevOrPreprod(env);
    }
}
