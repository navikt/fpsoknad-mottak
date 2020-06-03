package no.nav.foreldrepenger.mottak.oppslag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class SystemUserTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemUserTokenService.class);

    private final WebClient webClient;
    private SystemToken currentToken;

    public SystemUserTokenService(@Qualifier("STS") WebClient webClient) {
        this.webClient = webClient;
        currentToken = getUserToken();
    }

    private Mono<SystemToken> doFetch() {
        var token = webClient.get().accept(MediaType.APPLICATION_JSON).retrieve()
                .bodyToMono(SystemToken.class);
        return token;
    }

    public SystemToken getUserToken() {
        if (currentToken == null || currentToken.isExpired(20)) {
            currentToken = doFetch().block();
        }
        LOG.trace("Hentet JWT token {} for service user", currentToken.getToken());
        return currentToken;
    }

}
