package no.nav.foreldrepenger.mottak.tokendings;

import com.fasterxml.jackson.annotation.JsonProperty;

record TokendingsResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("issued_token_type") String issuedTokenType,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") String expiresIn) {
}
