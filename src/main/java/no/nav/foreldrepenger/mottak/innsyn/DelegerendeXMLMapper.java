package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsyn.XMLMapper.DELEGERENDE;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.domain.Søknad;

@Component
@Qualifier(DELEGERENDE)
public class DelegerendeXMLMapper implements XMLMapper {

    private final List<XMLMapper> mappers;

    private static final Logger LOG = LoggerFactory.getLogger(DelegerendeXMLMapper.class);

    public DelegerendeXMLMapper(XMLMapper... mappers) {
        this(asList(mappers));
    }

    @Inject
    public DelegerendeXMLMapper(List<XMLMapper> mappers) {
        this.mappers = mappers;
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        return mapper(xml, egenskap).tilSøknad(xml, egenskap);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return new MapperEgenskaper(mappers.stream()
                .map(m -> m.mapperEgenskaper())
                .map(e -> e.getSøknadEgenskaper())
                .flatMap(e -> e.stream())
                .collect(toList()));
    }

    private XMLMapper mapper(String xml, SøknadEgenskap egenskap) {
        XMLMapper m = mappers.stream()
                .filter(mapper -> mapper.kanMappe(egenskap))
                .findFirst()
                .orElse(new UkjentXMLMapper());
        LOG.info("Bruker mapper {}", m.getClass().getSimpleName());
        return m;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + "]";
    }
}
