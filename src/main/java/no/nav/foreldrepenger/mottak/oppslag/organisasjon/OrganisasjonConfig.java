package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "organisasjon")
public class OrganisasjonConfig {

    private final String baseUri;
    private final String pingPath;
    private final String organisasjonPath;
    private final boolean log;

    @ConstructorBinding
    public OrganisasjonConfig(String baseUri, @DefaultValue("/ping") String pingPath,
            @DefaultValue("/v1/organisasjon/") String organisasjonPath, boolean log) {
        this.baseUri = baseUri;
        this.pingPath = pingPath;
        this.organisasjonPath = organisasjonPath;
        this.log = log;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public String getPingPath() {
        return pingPath;
    }

    public String getorganisasjonPath() {
        return organisasjonPath;
    }

    public boolean isLog() {
        return log;
    }

}
