package no.nav.foreldrepenger.mottak.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import no.nav.foreldrepenger.mottak.http.FnrExtractor;
import no.nav.security.oidc.context.OIDCRequestContextHolder;

@Configuration
public class MottakConfiguration {

    @Bean
    @Qualifier("landkoder")
    public MessageSource landkoder() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("landkoder");
        return messageSource;
    }

    @Bean
    @Qualifier("kvitteringstekster")
    public MessageSource kvitteringstekster() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("kvitteringstekster");
        return messageSource;
    }

    // @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(false);
        loggingFilter.setIncludeQueryString(false);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(10000);
        return loggingFilter;
    }

    @Bean
    public FnrExtractor fnrExtractor(OIDCRequestContextHolder holder) {
        return new FnrExtractor(holder);
    }
}
