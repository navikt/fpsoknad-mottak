package no.nav.foreldrepenger.mottak.http.health;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoConnection;

@Component
public class FPInfoHealthIndicator extends EnvironmentAwareHealthIndicator {

    public FPInfoHealthIndicator(Environment env, FPInfoConnection connection) {
        super(env, connection);
    }
}
