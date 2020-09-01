package no.nav.foreldrepenger.mottak.util;

import java.util.HashMap;
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
        Map<String, String> versions = new HashMap<>();
        versions.put("image version", env.getProperty("nais.app.image"));
        builder.withDetail("versions", versions);
    }
}
