package no.nav.foreldrepenger.mottak.config;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

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

    @Bean
    @Profile(PREPROD)
    public CommonsRequestLoggingFilter loggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }
}
