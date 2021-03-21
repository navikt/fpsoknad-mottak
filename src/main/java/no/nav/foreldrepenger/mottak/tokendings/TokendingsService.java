package no.nav.foreldrepenger.mottak.tokendings;

import com.nimbusds.jose.JOSEException;

public interface TokendingsService {
    String exchangeToken(String token, TargetApp targetApp) throws JOSEException;
}
