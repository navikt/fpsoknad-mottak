package no.nav.foreldrepenger.mottak.innsending.mappers;

import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.mottak.util.Mappables.egenskaperFor;
import static no.nav.foreldrepenger.mottak.util.Mappables.mapperFor;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Component
@Qualifier(DELEGERENDE)
public class DelegerendeDomainMapper implements DomainMapper {

    private final List<DomainMapper> mappers;
    private final MapperEgenskaper mapperEgenskaper;

    public DelegerendeDomainMapper(DomainMapper... mappers) {
        this(asList(mappers));
    }

    @Inject
    public DelegerendeDomainMapper(List<DomainMapper> mappers) {
        this.mappers = mappers;
        this.mapperEgenskaper = egenskaperFor(mappers);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return mapperEgenskaper;
    }

    @Override
    public String tilXML(Søknad søknad, AktørId søker, SøknadEgenskap egenskap) {
        return mapperFor(mappers, egenskap).tilXML(søknad, søker, egenskap);
    }

    @Override
    public String tilXML(Endringssøknad endringssøknad, AktørId søker, SøknadEgenskap egenskap) {
        return mapperFor(mappers, egenskap).tilXML(endringssøknad, søker, egenskap);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + ", mapperEgenskaper=" + mapperEgenskaper + "]";
    }
}
