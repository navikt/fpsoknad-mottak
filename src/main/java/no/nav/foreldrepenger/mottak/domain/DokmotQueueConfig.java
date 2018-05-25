package no.nav.foreldrepenger.mottak.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "dokmot")
@Configuration
public class DokmotQueueConfig {

    @NotNull
    String hostname;
    @Positive
    int port;
    @NotNull
    String name;
    @NotNull
    String channelname;
    @NotNull
    String username;
    @NotNull
    String queuename;
    boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getChannelname() {
        return channelname;
    }

    public String getUsername() {
        return username;
    }

    public String getQueuename() {
        return queuename;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setQueuename(String queuename) {
        this.queuename = queuename;
    }

    public DokmotQueueConfig loggable() {
        DokmotQueueConfig unwrapped = new DokmotQueueConfig();
        unwrapped.setChannelname(getChannelname());
        unwrapped.setHostname(getHostname());
        unwrapped.setPort(getPort());
        unwrapped.setQueuename(getQueuename());
        return unwrapped;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [hostname=" + hostname + ", port=" + port + ", name=" + name
                + ", channelname="
                + channelname + ", username=" + "********" + ", queuename=" + queuename + "]";
    }

}
