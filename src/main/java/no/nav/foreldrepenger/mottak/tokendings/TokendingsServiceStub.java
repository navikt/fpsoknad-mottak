package no.nav.foreldrepenger.mottak.tokendings;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnLocal;

@ConditionalOnLocal
public class TokendingsServiceStub implements TokendingsService {

    @Override
    public String exchangeToken(String token, TokendingsTargetApp targetApp) {
        return "ey";
    }
}
