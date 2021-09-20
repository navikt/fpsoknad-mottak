package no.nav.foreldrepenger.mottak.innsending.mappers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.common.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V1SvangerskapspengerDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3EngangsstønadDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.common.oppslag.Oppslag;

@Configuration
public class DomainMapperConfiguration {

    @Bean
    public DomainMapper mapperForeldrepenger(Oppslag oppslag) {
        return new V3ForeldrepengerDomainMapper(oppslag);
    }

    @Bean
    public DomainMapper mapperEngangsstønad(Oppslag oppslag) {
        return new V3EngangsstønadDomainMapper(oppslag);
    }

    @Bean
    public DomainMapper mapperSVP() {
        return new V1SvangerskapspengerDomainMapper();
    }
}
