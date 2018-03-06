package no.nav.foreldrepenger.mottak.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "dokmot")
@Configuration
public class JallaConfig {

    String hostname;
    int port;
    String name;

    @Override
    public String toString() {
        return "JallaConfig [hostname=" + hostname + ", port=" + port + ", name=" + name + ", channelname="
                + channelname + ", username=" + username + ", queuename=" + queuename + "]";
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

    String channelname;
    String username;
    String queuename;

}
