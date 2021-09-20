package no.nav.foreldrepenger.mottak.innsending.varsel;

import java.net.URI;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.common.error.UnexpectedInputException;

@ConfigurationProperties(prefix = "varsel")
public class VarselConfig {

    private final String queueName;
    private final String channelname;
    private final boolean enabled;
    private final String username;
    private final URI uri;

    @ConstructorBinding
    public VarselConfig(URI uri, String queueName,
            String channelname, @DefaultValue("true") boolean enabled, String username) {
        this.uri = uri;
        this.queueName = queueName;
        this.channelname = channelname;
        this.enabled = enabled;
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getChannelname() {
        return channelname;
    }

    public int getPort() {
        return uri.getPort();
    }

    public String getName() {
        return Optional.ofNullable(uri.getPath())
                .filter(p -> p.startsWith("/"))
                .map(p -> p.substring(1))
                .orElseThrow(() -> new UnexpectedInputException("Navn %s ikke på korrekt format", uri.getPath()));
    }

    public String getQueueName() {
        return queueName;
    }

    public String getHostname() {
        return uri.getHost();
    }

    public String getUsername() {
        return username;
    }

    public URI getURI() {
        return uri;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [uri=" + uri + "]";
    }
}
