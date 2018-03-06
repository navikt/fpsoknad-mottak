[1mdiff --git a/src/main/java/no/nav/foreldrepenger/mottak/config/DokConfig.java b/src/main/java/no/nav/foreldrepenger/mottak/config/DokConfig.java[m
[1mindex 5a396da..636e50b 100644[m
[1m--- a/src/main/java/no/nav/foreldrepenger/mottak/config/DokConfig.java[m
[1m+++ b/src/main/java/no/nav/foreldrepenger/mottak/config/DokConfig.java[m
[36m@@ -5,17 +5,14 @@[m [mimport org.springframework.context.annotation.Configuration;[m
 [m
 @ConfigurationProperties(prefix = "dokmot")[m
 @Configuration[m
[31m-public class JallaConfig {[m
[32m+[m[32mpublic class DokConfig {[m
 [m
     String hostname;[m
     int port;[m
     String name;[m
[31m-[m
[31m-    @Override[m
[31m-    public String toString() {[m
[31m-        return "JallaConfig [hostname=" + hostname + ", port=" + port + ", name=" + name + ", channelname="[m
[31m-                + channelname + ", username=" + username + ", queuename=" + queuename + "]";[m
[31m-    }[m
[32m+[m[32m    String channelname;[m
[32m+[m[32m    String username;[m
[32m+[m[32m    String queuename;[m
 [m
     public String getHostname() {[m
         return hostname;[m
[36m@@ -65,8 +62,11 @@[m [mpublic class JallaConfig {[m
         this.queuename = queuename;[m
     }[m
 [m
[31m-    String channelname;[m
[31m-    String username;[m
[31m-    String queuename;[m
[32m+[m[32m    @Override[m
[32m+[m[32m    public String toString() {[m
[32m+[m[32m        return getClass().getSimpleName() + " [hostname=" + hostname + ", port=" + port + ", name=" + name[m
[32m+[m[32m                + ", channelname="[m
[32m+[m[32m                + channelname + ", username=" + username + ", queuename=" + queuename + "]";[m
[32m+[m[32m    }[m
 [m
 }[m
[1mdiff --git a/src/main/java/no/nav/foreldrepenger/mottak/dokmot/QueuePinger.java b/src/main/java/no/nav/foreldrepenger/mottak/dokmot/QueuePinger.java[m
[1mindex 718b412..6385adf 100644[m
[1m--- a/src/main/java/no/nav/foreldrepenger/mottak/dokmot/QueuePinger.java[m
[1m+++ b/src/main/java/no/nav/foreldrepenger/mottak/dokmot/QueuePinger.java[m
[36m@@ -3,12 +3,21 @@[m [mpackage no.nav.foreldrepenger.mottak.dokmot;[m
 import javax.inject.Inject;[m
 import javax.jms.JMSException;[m
 [m
[32m+[m[32mimport org.slf4j.Logger;[m
[32m+[m[32mimport org.slf4j.LoggerFactory;[m
 import org.springframework.jms.core.JmsTemplate;[m
 import org.springframework.stereotype.Component;[m
 [m
[32m+[m[32mimport no.nav.foreldrepenger.mottak.config.DokConfig;[m
[32m+[m
 @Component[m
 public class QueuePinger {[m
 [m
[32m+[m[32m    private static final Logger LOG = LoggerFactory.getLogger(QueuePinger.class);[m
[32m+[m
[32m+[m[32m    @Inject[m
[32m+[m[32m    private DokConfig jallConfig;[m
[32m+[m
     private final JmsTemplate dokmotTemplate;[m
 [m
     @Inject[m
[36m@@ -18,6 +27,7 @@[m [mpublic class QueuePinger {[m
 [m
     public void ping() {[m
         try {[m
[32m+[m[32m            LOG.info("Pinging queue {}", jallConfig);[m
             dokmotTemplate.getConnectionFactory().createConnection().close();[m
         } catch (JMSException e) {[m
             throw new RemoteUnavailableException(e);[m
[1mdiff --git a/src/main/resources/application.properties b/src/main/resources/application.properties[m
[1mindex 419b7eb..ec5184b 100644[m
[1m--- a/src/main/resources/application.properties[m
[1m+++ b/src/main/resources/application.properties[m
[36m@@ -11,17 +11,3 @@[m [mserver.servlet.contextPath=/api[m
 server.use-forward-headers=true[m
 info.app.version=@project.version@[m
 info.buildtime=@timestamp@[m
[31m-[m
[31m-DOKMOT_HOSTNAME=hostname[m
[31m-DOKMOT_PORT=2412[m
[31m-DOKMOT_NAME=name[m
[31m-DOKMOT_CHANNEL_NAME=channel[m
[31m-DOKMOT_USERNAME=username[m
[31m-DOKMOT_QUEUENAME=queue[m
[31m-[m
[31m-DOKMOT.HOSTNAME=hostname[m
[31m-DOKMOT.PORT=2412[m
[31m-DOKMOT.NAME=name[m
[31m-DOKMOT.CHANNEL_NAME=channel[m
[31m-DOKMOT.USERNAME=username[m
[31m-DOKMOT.QUEUENAME=queue[m
\ No newline at end of file[m
