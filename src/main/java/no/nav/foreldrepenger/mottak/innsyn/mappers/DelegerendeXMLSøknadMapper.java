package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.mottak.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.mottak.innsending.mappers.Mappables.egenskaperFor;
import static no.nav.foreldrepenger.mottak.innsending.mappers.Mappables.mapperFor;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;

@Component
@Qualifier(DELEGERENDE)
public class DelegerendeXMLSøknadMapper implements XMLSøknadMapper {
    private final List<XMLSøknadMapper> mappers;
    private final MapperEgenskaper mapperEgenskaper;

    public DelegerendeXMLSøknadMapper(XMLSøknadMapper... mappers) {
        this(asList(mappers));
    }

    @Inject
    public DelegerendeXMLSøknadMapper(List<XMLSøknadMapper> mappers) {
        this.mappers = mappers;
        this.mapperEgenskaper = egenskaperFor(mappers);
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        return mapperFor(mappers, egenskap).tilSøknad(xml, egenskap);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return mapperEgenskaper;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + ", mapperEgenskaper=" + mapperEgenskaper + "]";
    }
}
