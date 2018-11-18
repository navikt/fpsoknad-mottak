package no.nav.foreldrepenger.mottak.http.health;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.isDev;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.isPreprod;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.engangsstønad.DokmotQueuePinger;
import no.nav.foreldrepenger.mottak.innsending.engangsstønad.DokmotQueueUnavailableException;

@Component
public class DokmotHealthIndicator implements HealthIndicator, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(DokmotHealthIndicator.class);

    private final DokmotQueuePinger pinger;

    private Environment env;

    public DokmotHealthIndicator(DokmotQueuePinger pinger) {
        this.pinger = pinger;
    }

    @Override
    public Health health() {
        if (!isDev(env)) {
            try {
                pinger.ping();
                return isPreprod(env) ? upWithDetails() : up();
            } catch (DokmotQueueUnavailableException e) {
                LOG.warn("Kunne ikke sjekke helsen til DOKMOT {}", pinger.getQueueConfig(), e);
                return isPreprod(env) ? downWithDetails(e) : down();
            }
        }
        LOG.info(" DEV mode, sjekker ikke DOKMOT");
        return up();
    }

    private static Health down() {

        return Health.down().build();
    }

    private Health downWithDetails(Exception e) {
        return Health.down().withDetail("config", pinger.getQueueConfig().toString()).withException(e).build();
    }

    private static Health up() {
        return Health.up().build();
    }

    private Health upWithDetails() {
        return Health.up().withDetail("config", pinger.getQueueConfig().loggable()).build();
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pinger=" + pinger + "activeProfiles "
                + Arrays.toString(env.getActiveProfiles()) + "]";
    }

}
