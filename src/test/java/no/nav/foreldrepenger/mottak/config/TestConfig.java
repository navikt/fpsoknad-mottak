package no.nav.foreldrepenger.mottak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.Kjønn;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@Configuration
public class TestConfig {

    @Bean
    @Primary
    public Oppslag oppslagService() {
        return new Oppslag() {

            @Override
            public AktørId aktørId(Fødselsnummer fnr) {
                return new AktørId("1111111111");
            }

            @Override
            public Person person() {
                return TestUtils.person();
            }

            @Override
            public AktørId aktørId() {
                return new AktørId("1111111111");
            }

            @Override
            public Fødselsnummer fnr(AktørId aktørId) {
                return new Fødselsnummer("01010111111");
            }

            @Override
            public String ping() {
                return "42";
            }

            @Override
            public Navn navn(String fnr) {
                return new Navn("Ole", "Mellomnavn", "Olsen", Kjønn.M);
            }
        };
    }

}
