package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.util.Versjon.V2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.oppslag.Oppslag;

@Component
public class V2EngangsstønadXMLMapper extends AbstractXMLMapper {
    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V2, INITIELL_ENGANGSSTØNAD);
    private static final Logger LOG = LoggerFactory.getLogger(V2EngangsstønadXMLMapper.class);

    public V2EngangsstønadXMLMapper(Oppslag oppslag) {
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
