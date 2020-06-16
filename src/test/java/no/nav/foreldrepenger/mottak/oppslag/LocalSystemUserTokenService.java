package no.nav.foreldrepenger.mottak.oppslag;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnLocal;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemToken;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemTokenTjeneste;
import no.nav.security.token.support.core.jwt.JwtToken;

@Service
@ConditionalOnLocal
public class LocalSystemUserTokenService implements SystemTokenTjeneste {

    @Override
    public SystemToken getSystemToken() {
        return new SystemToken(new JwtToken("hey"), 3600L, "jalla");
    }

    @Override
    public String ping() {
        return "OK";
    }

}
