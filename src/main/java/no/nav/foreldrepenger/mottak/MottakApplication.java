package no.nav.foreldrepenger.mottak;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.hotspot.DefaultExports;

@SpringBootApplication
public class MottakApplication {

    public static void main(String[] args) {
        SpringApplication.run(MottakApplication.class, args);
    }

    @Bean
    CollectorRegistry prometheusCollector() {
        return CollectorRegistry.defaultRegistry;
    }

    @PostConstruct
    public void prometheusConfig() {
        DefaultExports.initialize();
    }
}