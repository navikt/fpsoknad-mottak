package no.nav.foreldrepenger.mottak.innsending.mappers;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.egenskaperFor;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.mapperFor;

@Component
@Qualifier(DELEGERENDE)
public class DelegerendeDomainMapper implements DomainMapper {

    private final List<DomainMapper> mappers;
    private final MapperEgenskaper mapperEgenskaper;

    public DelegerendeDomainMapper(DomainMapper... mappers) {
        this(asList(mappers));
    }

    @Autowired
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
