package no.nav.foreldrepenger.mottak.tokendings;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokendingsConfigurationMetadata(String issuer,
        @JsonProperty("token_endpoint") URI tokenEndpoint,
        @JsonProperty("jwks_uri") URI jwksUri) {
}
