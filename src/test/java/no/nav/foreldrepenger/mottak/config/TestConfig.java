package no.nav.foreldrepenger.mottak.config;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
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
            public List<Arbeidsforhold> getArbeidsforhold() {
                return Arrays.asList(
                        new Arbeidsforhold("1234", "", LocalDate.now().minusDays(200),
                                Optional.of(LocalDate.now()), new ProsentAndel(90.0), "El Bedrifto"),
                        new Arbeidsforhold("2345", "", LocalDate.now().minusDays(300),
                                Optional.of(LocalDate.now().minusDays(240)), new ProsentAndel(55.0), "Bedriftolainen"));
            }

            @Override
            public String ping() {
                return "42";
            }
        };
    }

}
