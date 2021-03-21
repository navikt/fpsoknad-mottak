package no.nav.foreldrepenger.mottak.tokendings;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;

record TokendingsMetadata(String issuer,
        @JsonProperty("token_endpoint") URI tokenEndpoint,
        @JsonProperty("jwks_uri") URI jwksUri) {
}
