package no.nav.foreldrepenger.mottak.http.health;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynConnection;

@Component
public class FPInfoHealthIndicator extends EnvironmentAwareHealthIndicator {

    public FPInfoHealthIndicator(Environment env, InnsynConnection connection) {
        super(env, connection);
    }
}
