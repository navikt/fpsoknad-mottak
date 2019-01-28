package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.errorhandling.UnsupportedVersionException;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public class DelegerendeDomainMapper implements VersjonsBevisstDomainMapper {

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
        List<SøknadEgenskap> e = new ArrayList<>();
        for (DomainMapper mapper : mappers) {
            MapperEgenskaper mapperEgenskaper = mapper.mapperEgenskaper();
            List<SøknadEgenskap> søknadEgenskaper = mapperEgenskaper.getSøknadEgenskaper();
            e.addAll(søknadEgenskaper);
        }
        return new MapperEgenskaper(e);

    }

    @Override
    public String tilXML(Søknad søknad, AktorId søker, Versjon versjon) {
        return mapper(versjon).tilXML(søknad, søker);
    }

    @Override
    public String tilXML(Endringssøknad endringssøknad, AktorId søker, Versjon versjon) {
        return mapper(versjon).tilXML(endringssøknad, søker);
    }

    private DomainMapper mapper(Versjon versjon) {
        return mappers.stream()
                .filter(s -> s.kanMappe(versjon))
                .findFirst()
                .orElseThrow(() -> new UnsupportedVersionException("Versjon " + versjon + " ikke støttet", versjon));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + "]";
    }

}
