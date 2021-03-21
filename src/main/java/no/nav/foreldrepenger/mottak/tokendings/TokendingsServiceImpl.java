package no.nav.foreldrepenger.mottak.tokendings;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;

@Service
@ConditionalOnK8s
public class TokendingsServiceImpl implements TokendingsService {

    private final TokendingsConnection connection;

    public TokendingsServiceImpl(TokendingsConnection connection, TokendingsConfig cfg) {
        this.connection = connection;
    }

    @Override
    public String exchangeToken(String token, TokendingsTargetApp targetApp) {
        return connection.exchange(token, targetApp).accessToken();
    }
}
