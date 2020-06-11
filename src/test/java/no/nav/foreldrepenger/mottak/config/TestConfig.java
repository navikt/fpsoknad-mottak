package no.nav.foreldrepenger.mottak.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@Configuration
public class TestConfig {

    @Bean
    @Primary
    public Oppslag oppslagService() {
        return new Oppslag() {

            @Override
            public AktørId getAktørId(Fødselsnummer fnr) {
                return new AktørId("1111111111");
            }

            @Override
            public Person getSøker() {
                return TestUtils.person();
            }

            @Override
            public AktørId getAktørId() {
                return new AktørId("1111111111");
            }

            @Override
            public Fødselsnummer getFnr(AktørId aktørId) {
                return new Fødselsnummer("01010111111");
            }

            @Override
            public String getAktørIdAsString() {
                return Optional.ofNullable(getAktørId())
                        .map(AktørId::getId)
                        .orElse(null);
            }

            @Override
            public String ping() {
                return "42";
            }

            @Override
            public Navn hentNavn(String fnr) {
                return new Navn("Ole", "Olsen", fnr, Kjønn.M);
            }
        };
    }

}
