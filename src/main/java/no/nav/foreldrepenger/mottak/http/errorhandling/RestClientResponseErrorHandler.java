package no.nav.foreldrepenger.mottak.http.errorhandling;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.StreamUtils.copyToString;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class RestClientResponseErrorHandler extends DefaultResponseErrorHandler {

    private final Logger LOG = LoggerFactory.getLogger(RestClientResponseErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        LOG.debug("Håndterer feilrespons med kode {}", response.getStatusCode());
        String feilrespons = copyToString(response.getBody(), UTF_8);
        LOG.debug("Feilrespons er: {}", feilrespons);
        switch (response.getStatusCode()) {
        case FORBIDDEN:
            LOG.warn(FORBIDDEN + ". kaster ForbiddenException");
            throw new ForbiddenException(feilrespons);
        default:
            super.handleError(response);
        }
    }
}