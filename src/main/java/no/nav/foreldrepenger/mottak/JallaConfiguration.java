package no.nav.foreldrepenger.mottak;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JallaConfiguration {

    @Bean
    public Dummy dummy() {
        return new Dummy();
    }

    static class Dummy {

        public void hello() {
            System.out.println("hello");
        }
    }

}
