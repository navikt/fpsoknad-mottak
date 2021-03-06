package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.util.Versjon.V3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@Component
public class V3EngangsstønadXMLMapper extends AbstractXMLMapper {
    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V3, INITIELL_ENGANGSSTØNAD);
    private static final Logger LOG = LoggerFactory.getLogger(V3EngangsstønadXMLMapper.class);

    public V3EngangsstønadXMLMapper(Oppslag oppslag) {
        super(oppslag);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return EGENSKAPER;
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        LOG.info("Mapper ikke {} tilbake til søknad foreløpig", mapperEgenskaper());
        return null;
    }
}
