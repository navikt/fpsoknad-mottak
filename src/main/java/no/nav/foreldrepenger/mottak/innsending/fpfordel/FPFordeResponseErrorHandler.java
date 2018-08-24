package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;

import no.nav.foreldrepenger.mottak.http.ForbiddenException;

class FPFordeResponseErrorHandler extends DefaultResponseErrorHandler {

    private final Logger LOG = LoggerFactory.getLogger(FPFordeResponseErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        LOG.debug("Håndterer feilrespons med returkode {}", response.getStatusCode());
        String bodyText = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        LOG.debug("Respons body: {}", bodyText);
        if (response.getStatusCode() == FORBIDDEN) {
            LOG.warn(FORBIDDEN + ". Throwing ForbiddenException");
            throw new ForbiddenException(bodyText);
        }

        super.handleError(response);
    }
}