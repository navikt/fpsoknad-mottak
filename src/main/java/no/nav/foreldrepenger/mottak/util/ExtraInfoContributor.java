package no.nav.foreldrepenger.mottak.util;

import static java.lang.management.ManagementFactory.getRuntimeMXBean;
import static java.time.Instant.ofEpochMilli;
import static no.nav.foreldrepenger.boot.conditionals.Cluster.NAIS_CLUSTER_NAME;
import static no.nav.foreldrepenger.boot.conditionals.Cluster.NAIS_NAMESPACE_NAME;

import java.time.ZoneId;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ExtraInfoContributor implements InfoContributor {

    @Autowired
    Environment env;

    @Override
    public void contribute(Builder builder) {
        builder.withDetail("Extras", Map.of(
                "startup time", ofEpochMilli(getRuntimeMXBean().getStartTime()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                "cluster name", env.getProperty(NAIS_CLUSTER_NAME),
                "namespace name", env.getProperty(NAIS_NAMESPACE_NAME),
                "image name", env.getProperty("nais.app.image")));
    }
}
