package no.nav.foreldrepenger.mottak.tokendings;

//@ConditionalOnLocal
public class TokendingsServiceStub implements TokendingsService {

    @Override
    public String exchangeToken(String token, TokendingsTargetApp targetApp) {
        return "ey";
    }
}
