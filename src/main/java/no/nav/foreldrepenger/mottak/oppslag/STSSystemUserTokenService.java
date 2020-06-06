package no.nav.foreldrepenger.mottak.oppslag;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;

@Service
@ConditionalOnK8s
public class STSSystemUserTokenService implements SystemUserTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(STSSystemUserTokenService.class);

    private final WebClient webClient;
    private SystemToken currentToken;

    public STSSystemUserTokenService(@Qualifier("STS") WebClient webClient) {
        this.webClient = webClient;
        currentToken = getSystemToken();
    }

    private SystemToken refresh() {
        return webClient.get()
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SystemToken.class)
                .block();
    }

    @Override
    public SystemToken getSystemToken() {
        if (currentToken == null || currentToken.isExpired(20)) {
            currentToken = refresh();
        }
        LOG.trace("Hentet JWT token {} for service user", currentToken);
        return currentToken;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[webClient=" + webClient + ", currentToken=" + currentToken + "]";
    }

}
