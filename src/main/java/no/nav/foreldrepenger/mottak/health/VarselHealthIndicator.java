package no.nav.foreldrepenger.mottak.health;

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

import no.nav.foreldrepenger.mottak.innsending.varsel.VarselConnection;

@Component
public class VarselHealthIndicator implements HealthIndicator, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(VarselHealthIndicator.class);

    private final VarselConnection connection;

    private Environment env;

    public VarselHealthIndicator(VarselConnection connection) {
        this.connection = connection;
    }

    @Override
    public Health health() {
        if (!isDev(env)) {
            try {
                connection.ping();
                return isPreprod(env) ? upWithDetails() : up();
            } catch (Exception e) {
                LOG.warn("Kunne ikke sjekke helsen til {} på {}", connection.name(), connection.pingEndpoint(), e);
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
        return Health.down().withDetail("config", connection.pingEndpoint()).withException(e).build();
    }

    private static Health up() {
        return Health.up().build();
    }

    private Health upWithDetails() {
        return Health.up().withDetail("config", connection.pingEndpoint()).build();
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + "activeProfiles "
                + Arrays.toString(env.getActiveProfiles()) + "]";
    }

}
