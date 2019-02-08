package no.nav.foreldrepenger.mottak.innsyn.engangsstønad;

import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.mappers.AbstractXMLMapper;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

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
        // TODO
        LOG.info("Mapper ikke engangssøknad V2 tilbake til søknad foreløpig");
        return null;
    }

}
