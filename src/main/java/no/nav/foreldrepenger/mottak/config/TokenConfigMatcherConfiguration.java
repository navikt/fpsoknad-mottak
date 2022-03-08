package no.nav.foreldrepenger.mottak.config;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import com.google.common.base.Splitter;

import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import no.nav.security.token.support.client.spring.oauth2.ClientConfigurationPropertiesMatcher;

public class TokenConfigMatcherConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TokenConfigMatcherConfiguration.class);

    @Bean
    public ClientConfigurationPropertiesMatcher configFinder() {
        return new ClientConfigurationPropertiesMatcher() {
            @Override
            public Optional<ClientProperties> findProperties(ClientConfigurationProperties properties, URI uri) {
                LOG.trace("Oppslag token X konfig for {}", uri.getHost());
                var cfg = properties.getRegistration().get(Splitter.on(".").splitToList(uri.getHost()).get(0));
                if (cfg == null) {
                    cfg = properties.getRegistration().get(Splitter.on("/").splitToList(uri.getPath()).get(1));
                }

                if (cfg != null) {
                    LOG.trace("Oppslag token X konfig for {} OK", uri.getHost());
                } else {
                    LOG.trace("Oppslag token X konfig for {} fant ingenting", uri.getHost());
                }
                return Optional.ofNullable(cfg);
            }
        };
    }
}
