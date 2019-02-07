package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.Mappable.DELEGERENDE;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.errorhandling.UnsupportedEgenskapException;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Component
@Qualifier(DELEGERENDE)
public class DelegerendeDomainMapper implements DomainMapper {

    private final List<DomainMapper> mappers;

    public DelegerendeDomainMapper(DomainMapper... mappers) {
        this(asList(mappers));
    }

    @Inject
    public DelegerendeDomainMapper(List<DomainMapper> mappers) {
        this.mappers = mappers;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return new MapperEgenskaper(mappers.stream()
                .map(m -> m.mapperEgenskaper())
                .map(e -> e.getSøknadEgenskaper())
                .flatMap(e -> e.stream())
                .collect(toList()));
    }

    @Override
    public String tilXML(Søknad søknad, AktorId søker, SøknadEgenskap egenskap) {
        return mapper(egenskap).tilXML(søknad, søker, egenskap);
    }

    @Override
    public String tilXML(Endringssøknad endringssøknad, AktorId søker, SøknadEgenskap egenskap) {
        return mapper(egenskap).tilXML(endringssøknad, søker, egenskap);
    }

    private DomainMapper mapper(SøknadEgenskap egenskap) {
        return mappers.stream()
                .filter(mapper -> mapper.kanMappe(egenskap))
                .findFirst()
                .orElseThrow(() -> new UnsupportedEgenskapException(egenskap));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + "]";
    }

}
