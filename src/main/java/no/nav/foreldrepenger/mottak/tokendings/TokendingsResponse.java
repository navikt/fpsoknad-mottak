package no.nav.foreldrepenger.mottak.tokendings;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TokendingsResponse(
        String accessToken,
        String issuedTokenType,
        String tokenType,
        String expiresIn) {
}
