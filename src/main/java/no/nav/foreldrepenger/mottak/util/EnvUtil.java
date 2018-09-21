package no.nav.foreldrepenger.mottak.util;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.core.env.Environment;

public final class EnvUtil {

    public static final Marker CONFIDENTIAL = MarkerFactory.getMarker("CONFIDENTIAL");

    private EnvUtil() {

    }

    public static boolean isDevOrPreprod(Environment env) {
        return env == null || env.acceptsProfiles("dev", "preprod");
    }

    public static boolean isProd(Environment env) {
        return !isDevOrPreprod(env);
    }
}
