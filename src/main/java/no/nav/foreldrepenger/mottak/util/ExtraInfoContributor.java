package no.nav.foreldrepenger.mottak.util;

import static java.lang.management.ManagementFactory.getRuntimeMXBean;
import static java.time.Instant.ofEpochMilli;
import static no.nav.boot.conditionals.Cluster.NAIS_CLUSTER_NAME;
import static no.nav.boot.conditionals.Cluster.NAIS_IMAGE_NAME;
import static no.nav.boot.conditionals.Cluster.NAIS_NAMESPACE_NAME;

import java.time.ZoneId;
import java.util.Map;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ExtraInfoContributor implements InfoContributor {

    private final Environment env;

    public ExtraInfoContributor(Environment env) {
        this.env = env;
    }

    @Override
    public void contribute(Builder builder) {
        builder.withDetail("Cluster information", Map.of(
                "Java version", env.getProperty("java.version"),
                "Startup time", ofEpochMilli(getRuntimeMXBean().getStartTime()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                "Cluster name", env.getProperty(NAIS_CLUSTER_NAME),
                "Namespace name", env.getProperty(NAIS_NAMESPACE_NAME),
                "Image name", env.getProperty(NAIS_IMAGE_NAME)));
    }
}
