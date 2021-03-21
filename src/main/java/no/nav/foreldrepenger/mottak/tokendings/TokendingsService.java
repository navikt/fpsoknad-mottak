package no.nav.foreldrepenger.mottak.tokendings;

public interface TokendingsService {

    String exchangeToken(String token, TokendingsTargetApp targetApp);
}
