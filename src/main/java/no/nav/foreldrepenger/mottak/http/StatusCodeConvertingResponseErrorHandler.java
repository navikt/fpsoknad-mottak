package no.nav.foreldrepenger.mottak.http;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;

import no.nav.foreldrepenger.mottak.http.errorhandling.NotFoundException;
import no.nav.foreldrepenger.mottak.http.errorhandling.UnauthenticatedException;
import no.nav.foreldrepenger.mottak.http.errorhandling.UnauthorizedException;
import no.nav.foreldrepenger.mottak.util.TokenHelper;

//TODO, not yet used
public class StatusCodeConvertingResponseErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(StatusCodeConvertingResponseErrorHandler.class);
    private final TokenHelper tokenHelper;

    public StatusCodeConvertingResponseErrorHandler(TokenHelper tokenHelper) {
        this.tokenHelper = tokenHelper;
    }

    @Override
    protected void handleError(ClientHttpResponse res, HttpStatus code) throws IOException {
        LOG.info("Handling error code {}", code);
        switch (code) {
        case NOT_FOUND:
            throw new NotFoundException(res.getStatusText(), new HttpClientErrorException(code));
        case UNAUTHORIZED:
            throw new UnauthorizedException(res.getStatusText(), tokenHelper.getExp(),
                    new HttpClientErrorException(code));
        case FORBIDDEN:
            throw new UnauthenticatedException(res.getStatusText(), tokenHelper.getExp(),
                    new HttpClientErrorException(code));
        default:
            super.handleError(res, code);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [tokenHandler=" + tokenHelper + "]";
    }
}
