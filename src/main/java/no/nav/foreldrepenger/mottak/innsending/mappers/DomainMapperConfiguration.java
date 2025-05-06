package no.nav.foreldrepenger.mottak.innsending.mappers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.common.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V1SvangerskapspengerDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3EngangsstønadDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.mottak.oversikt.OversiktTjeneste;

@Configuration
public class DomainMapperConfiguration {

    @Bean
    public DomainMapper mapperForeldrepenger(OversiktTjeneste oversiktTjeneste) {
        return new V3ForeldrepengerDomainMapper(oversiktTjeneste);
    }

    @Bean
    public DomainMapper mapperEngangsstønad(OversiktTjeneste oversiktTjeneste) {
        return new V3EngangsstønadDomainMapper(oversiktTjeneste);
    }

    @Bean
    public DomainMapper mapperSVP() {
        return new V1SvangerskapspengerDomainMapper();
    }
}
