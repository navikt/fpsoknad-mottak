package no.nav.foreldrepenger.mottak.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class MottakConfiguration {

    public static final String LANDKODER = "landkoder";
    public static final String KVITTERINGSTEKSTER = "kvitteringstekster";

    @Bean
    @Qualifier(LANDKODER)
    public MessageSource landkoderSource() {
        var messageSource = new LoggingMessageSource();
        messageSource.setBasename(LANDKODER);
        return messageSource;
    }

    @Bean
    @Qualifier(KVITTERINGSTEKSTER)
    public MessageSource kvitteringsteksterSource() {
        var messageSource = new LoggingMessageSource();
        messageSource.setBasename(KVITTERINGSTEKSTER);
        return messageSource;
    }

    static class LoggingMessageSource extends ResourceBundleMessageSource {
        private static final Logger LOG = LoggerFactory.getLogger(LoggingMessageSource.class);
        @Override
        protected String getMessageInternal(String code, Object[] args, Locale locale) {
            var message = super.getMessageInternal(code, args, locale);
            if (message == null) {
                var basename = String.join(", ", super.getBasenameSet());
                LOG.warn("Finner ikke nøkkel '{}' i messagebundle '{}' for locale '{}'", code, basename, locale);
            }
            return message;
        }
    }
}
