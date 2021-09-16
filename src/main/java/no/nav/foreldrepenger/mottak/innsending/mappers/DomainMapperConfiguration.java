package no.nav.foreldrepenger.mottak.innsending.mappers;

import java.util.List;

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
    public List<DomainMapper> mappers(Oppslag oppslag) {
        return List.of(
            new V1SvangerskapspengerDomainMapper(),
            new V3EngangsstønadDomainMapper(oppslag),
            new V3ForeldrepengerDomainMapper(oppslag));

    }
}
