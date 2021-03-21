package no.nav.foreldrepenger.mottak.tokendings;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;

@ConditionalOnK8s
public class TokendingsServiceImpl implements TokendingsService {

    private final TokendingsConnection connection;

    public TokendingsServiceImpl(TokendingsConnection connection) {
        this.connection = connection;
    }

    @Override
    public String exchangeToken(TokendingsTargetApp targetApp) {
        return connection.exchange(targetApp).accessToken();
    }
}
