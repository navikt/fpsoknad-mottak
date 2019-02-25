package no.nav.foreldrepenger.mottak.innsending.varsel;

import java.net.URI;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

@ConfigurationProperties(prefix = "varsel")
@Configuration
public class VarselConfig {

    @NotNull
    String hostname;
    @Positive
    int port;
    @NotNull
    String name;
    @NotNull
    String queueName;
    @NotNull
    String channelname;
    boolean enabled = false;
    @NotNull
    String username;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getChannelname() {
        return channelname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
