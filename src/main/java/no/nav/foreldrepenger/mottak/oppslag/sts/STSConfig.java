package no.nav.foreldrepenger.mottak.oppslag.sts;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

@ConfigurationProperties(prefix = "sts")
public class STSConfig extends AbstractConfig {

    private final String username;
    private final String password;
    private final Duration slack;

    @ConstructorBinding
    public STSConfig(String baseUri, @DefaultValue("20s") Duration slack, String username, String password,
            @DefaultValue("/ping") String pingPath,
            boolean log) {
        super(baseUri, pingPath, log);
        this.slack = slack;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Duration getSlack() {
        return slack;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[username=" + username + ", password=" + password + ", slack=" + slack
                + "]";
    }

}
