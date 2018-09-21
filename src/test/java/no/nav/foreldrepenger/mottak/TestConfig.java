package no.nav.foreldrepenger.mottak;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.http.Oppslag;
import no.nav.foreldrepenger.mottak.pdf.Arbeidsforhold;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Configuration
public class TestConfig {

    @Bean
    @Primary
    public Oppslag oppslagService() {
        return new Oppslag() {

            @Override
            public AktorId getAktørId(Fødselsnummer fnr) {
                return new AktorId("1111111111");
            }

            @Override
            public Person getSøker() {
                return TestUtils.person();
            }

            @Override
            public AktorId getAktørId() {
                return new AktorId("1111111111");
            }

            @Override
            public Fødselsnummer getFnr(AktorId aktørId) {
                return new Fødselsnummer("01010111111");
            }

            @Override
            public List<Arbeidsforhold> getArbeidsforhold() {
                return Arrays.asList(
                    new Arbeidsforhold("El Bedrifto", LocalDate.now().minusDays(200), LocalDate.now(), 90),
                    new Arbeidsforhold("Bedriftolainen", LocalDate.now().minusDays(300), LocalDate.now(), 60)
                );
            }
        };
    }

}
