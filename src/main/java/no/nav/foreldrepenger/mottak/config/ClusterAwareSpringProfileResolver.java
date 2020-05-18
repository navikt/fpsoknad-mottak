package no.nav.foreldrepenger.mottak.config;

import static java.lang.System.getenv;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEFAULT;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.LOCAL;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterAwareSpringProfileResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterAwareSpringProfileResolver.class);
    private static final String NAIS_CLUSTER_NAME = "NAIS_CLUSTER_NAME";

    public static String[] profiles() {
        return Optional.ofNullable(clusterFra(getenv(NAIS_CLUSTER_NAME)))
                .map(c -> new String[] { c })
                .orElse(new String[0]);
    }

    private static String clusterFra(String cluster) {
        if (cluster == null) {
            LOG.info("NAIS cluster ikke detektert, antar {}", LOCAL);
            System.setProperty(NAIS_CLUSTER_NAME, LOCAL);
            return LOCAL;
        }
        if (cluster.contains(DEV)) {
            return DEV;
        }
        return DEFAULT;
    }
}
