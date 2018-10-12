package no.nav.foreldrepenger.stub;

import no.nav.foreldrepenger.lookup.rest.sak.SakClient;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.ArbeidsforholdClient;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.OrganisasjonClient;
import no.nav.foreldrepenger.lookup.ws.person.PersonClient;
import no.nav.foreldrepenger.lookup.ws.ytelser.fpsak.FpsakClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class StubConfiguration {

    @Bean
    @Primary
    public AktorIdClient getAktorIdClientStub() {
        return new AktorIdClientStub();
    }

    @Bean
    @Primary
    public PersonClient getPersonClientStub() {
        return new PersonClientStub();
    }

    @Bean
    @Primary
    public ArbeidsforholdClient getAaregClientStub() {
        return new ArbeidsforholdClientStub();
    }

    @Bean
    @Primary
    public FpsakClient getFpsakClientStub() {
        return new FpsakClientStub();
    }

    @Bean
    @Primary
    public OrganisasjonClient organisasjonClientStub() {
        return new OrganisasjonClientStub();
    }

    @Bean
    @Primary
    public SakClient sakClientStub() {
        return new SakClientStub();
    }
}
