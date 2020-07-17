package no.nav.foreldrepenger.mottak.innsending.varsel;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "varsel")
public class VarselConfig {


    private final String queueName;
    private final String channelname;
    private final boolean enabled;
    private final String username;
    private final URI uri;


    @ConstructorBinding
    public VarselConfig(URI uri, String queueName,
            String channelname, boolean enabled, String username) {
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
        return uri.getPath();
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
