package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsending.mappers.Mappable.DELEGERENDE;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Component
@Qualifier(DELEGERENDE)
public class DelegerendeXMLMapper implements XMLMapper {

    private final List<XMLMapper> mappers;

    private final MapperEgenskaper mapperEgenskaper;

    private static final Logger LOG = LoggerFactory.getLogger(DelegerendeXMLMapper.class);

    public DelegerendeXMLMapper(XMLMapper... mappers) {
        this(asList(mappers));
    }

    @Inject
    public DelegerendeXMLMapper(List<XMLMapper> mappers) {
        this.mappers = mappers;
        this.mapperEgenskaper = mapperEgenskaper(mappers);
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        return mapper(egenskap).tilSøknad(xml, egenskap);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return mapperEgenskaper;
    }

    private XMLMapper mapper(SøknadEgenskap egenskap) {
        XMLMapper mapper = mappers.stream()
                .filter(m -> m.kanMappe(egenskap))
                .findFirst()
                .orElse(new UkjentXMLMapper());
        LOG.info("Bruker mapper {} for {}", mapper.getClass().getSimpleName(), egenskap);
        return mapper;
    }

    private static MapperEgenskaper mapperEgenskaper(List<XMLMapper> mappers) {
        return new MapperEgenskaper(mappers.stream()
                .map(e -> e.mapperEgenskaper())
                .map(e -> e.getEgenskaper())
                .flatMap(e -> e.stream())
                .collect(toList()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + ", mapperEgenskaper=" + mapperEgenskaper + "]";
    }
}
