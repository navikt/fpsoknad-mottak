package no.nav.foreldrepenger.mottak.innsending.varsel;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.web.util.UriComponentsBuilder;

@ConfigurationProperties(prefix = "varsel")
public class VarselConfig {

    private final String hostname;
    private final int port;
    private final String name;

    private final String queueName;
    private final String channelname;
    private final boolean enabled;
    private final String username;

    @ConstructorBinding
    public VarselConfig(String hostname, int port, String name, String queueName,
            String channelname, boolean enabled, String username) {
        this.hostname = hostname;
        this.port = port;
        this.name = name;
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
        return port;
    }

    public String getName() {
        return name;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public URI getURI() {
        return UriComponentsBuilder
                .newInstance().scheme("jms")
                .userInfo(getUsername())
                .host(getHostname())
                .port(getPort())
                .pathSegment(getChannelname(), getQueueName())
                .queryParam("enabled", isEnabled())
                .build().toUri();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [uri=" + getURI() + "]";
    }
}
