package no.nav.foreldrepenger.mottak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaUtils;
import org.springframework.kafka.support.LoggingProducerListener;

import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID;

@Configuration
public class KafkaConfiguration {

    @Bean
    public LoggingProducerListener<String, String> loggingProducerListener() {
        LoggingProducerListener<String, String> loggingProducerListener = new LoggingProducerListener<>();
        KafkaUtils.setProducerRecordFormatter(r -> String.format("Feil mot topic %s for callId %s" + r.topic() + r.headers().headers(NAV_CALL_ID)));
        return loggingProducerListener;
    }

}
