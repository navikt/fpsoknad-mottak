package no.nav.foreldrepenger.lookup.rest.fpinfo;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;
import java.nio.charset.Charset;

import no.nav.foreldrepenger.errorhandling.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class FPInfoResponseErrorHandler extends DefaultResponseErrorHandler {

    private final Logger LOG = LoggerFactory.getLogger(FPInfoResponseErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        LOG.debug("Error response handler executing for response {}", response);

        if (response.getStatusCode() == FORBIDDEN) {
            LOG.warn(FORBIDDEN + ". Throwing ForbiddenException exception");
            throw new ForbiddenException(StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        }
        super.handleError(response);
    }

}
