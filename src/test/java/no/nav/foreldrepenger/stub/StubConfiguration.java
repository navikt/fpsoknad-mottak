package no.nav.foreldrepenger.stub;

import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.ArbeidsforholdClient;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.OrganisasjonClient;
import no.nav.foreldrepenger.lookup.ws.inntekt.InntektClient;
import no.nav.foreldrepenger.lookup.ws.medl.MedlClient;
import no.nav.foreldrepenger.lookup.ws.person.PersonClient;
import no.nav.foreldrepenger.lookup.ws.ytelser.arena.ArenaClient;
import no.nav.foreldrepenger.lookup.ws.ytelser.fpsak.FpsakClient;
import no.nav.foreldrepenger.lookup.ws.ytelser.gsak.GsakClient;
import no.nav.foreldrepenger.lookup.ws.ytelser.infotrygd.InfotrygdClient;
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
    public ArenaClient getArenaClientStub() {
        return new ArenaClientStub();
    }

    @Bean
    @Primary
    public FpsakClient getFpsakClientStub() {
        return new FpsakClientStub();
    }

    @Bean
    @Primary
    public InfotrygdClient getInfotrygdClientStub() {
        return new InfotrygdClientStub();
    }

    @Bean
    @Primary
    public InntektClient getInntektClientStub() {
        return new InntektClientStub();
    }

    @Bean
    @Primary
    public MedlClient getMedlClientStub() {
        return new MedlClientStub();
    }

    @Bean
    @Primary
    public OrganisasjonClient organisasjonClientStub() {
        return new OrganisasjonClientStub();
    }

    @Bean
    @Primary
    public GsakClient gsakClientStub() {
        return new GsakClientStub();
    }
}
