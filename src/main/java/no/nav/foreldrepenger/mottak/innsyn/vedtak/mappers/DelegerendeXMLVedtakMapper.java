package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.mottak.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.mottak.innsending.mappers.Mappables.egenskaperFor;
import static no.nav.foreldrepenger.mottak.innsending.mappers.Mappables.mapperFor;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;

@Qualifier(DELEGERENDE)
@Component
public class DelegerendeXMLVedtakMapper implements XMLVedtakMapper {

    private final List<XMLVedtakMapper> mappers;
    private final MapperEgenskaper mapperEgenskaper;

    public DelegerendeXMLVedtakMapper(XMLVedtakMapper... mappers) {
        this(asList(mappers));
    }

    @Inject
    public DelegerendeXMLVedtakMapper(List<XMLVedtakMapper> mappers) {
        this.mappers = mappers;
        this.mapperEgenskaper = egenskaperFor(mappers);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return mapperEgenskaper;
    }

    @Override
    public Vedtak tilVedtak(String xml, SøknadEgenskap egenskap) {
        return mapperFor(mappers, egenskap).tilVedtak(xml, egenskap);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + ", mapperEgenskaper=" + mapperEgenskaper + "]";
    }

}
