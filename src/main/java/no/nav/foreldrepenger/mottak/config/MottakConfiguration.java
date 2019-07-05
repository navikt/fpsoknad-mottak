package no.nav.foreldrepenger.mottak.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MottakConfiguration {
    @Value("${test:jalla}")
    private String test;

    private static final Logger LOG = LoggerFactory.getLogger(MottakConfiguration.class);
    public static final String LANDKODER = "landkoder";
    public static final String KVITTERINGSTEKSTER = "kvitteringstekster";

    @Bean
    @Qualifier(LANDKODER)
    public MessageSource landkoder() {
        LOG.info("XXXXXXXXXXXXXX " + test);
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(LANDKODER);
        return messageSource;
    }

    @Bean
    @Qualifier(KVITTERINGSTEKSTER)
    public MessageSource kvitteringstekster() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(KVITTERINGSTEKSTER);
        return messageSource;
    }
}
