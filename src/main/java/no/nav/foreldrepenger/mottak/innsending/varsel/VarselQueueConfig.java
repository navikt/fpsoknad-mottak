package no.nav.foreldrepenger.mottak.innsending.varsel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@ConfigurationProperties(prefix = "varsel")
@Configuration
public class VarselQueueConfig {

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

    public VarselQueueConfig loggable() {
        VarselQueueConfig unwrapped = new VarselQueueConfig();
        unwrapped.setChannelname(getChannelname());
        unwrapped.setHostname(getHostname());
        unwrapped.setPort(getPort());
        unwrapped.setQueueName(getQueueName());
        return unwrapped;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [hostname=" + hostname + ", port=" + port + ", name=" + name
            + ", channelname="
            + channelname + ", queuename=" + queueName + "]";
    }
}
