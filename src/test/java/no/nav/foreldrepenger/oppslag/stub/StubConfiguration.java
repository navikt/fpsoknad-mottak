package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.http.lookup.aareg.AaregClient;
import no.nav.foreldrepenger.oppslag.http.lookup.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.arena.ArenaClient;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.fpsak.FpsakClient;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.infotrygd.InfotrygdClient;
import no.nav.foreldrepenger.oppslag.http.lookup.inntekt.InntektClient;
import no.nav.foreldrepenger.oppslag.http.lookup.medl.MedlClient;
import no.nav.foreldrepenger.oppslag.http.lookup.person.PersonClient;
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
    public AaregClient getAaregClientStub() {
        return new AaregClientStub();
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
}
