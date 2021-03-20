package no.nav.foreldrepenger.mottak.tokendings;

import java.net.URI;
import java.net.URL;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record TokendingsConfigurationMetadata(String issuer, URL tokenEndpoint, URI jwksUri) {
}
