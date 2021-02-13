package no.nav.foreldrepenger.mottak.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ImageVersionContributor implements InfoContributor {

    @Autowired
    Environment env;

    @Override
    public void contribute(Builder builder) {
        builder.withDetail("Extras", Map.of(
                "cluster", env.getProperty("nais.cluster.name"),
                "image version", env.getProperty("nais.app.image")));
    }
}
