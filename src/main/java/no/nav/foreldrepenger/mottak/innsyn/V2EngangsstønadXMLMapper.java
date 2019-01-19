package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Collections.singletonList;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENGANGSSØKNAD;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@Component
public class V2EngangsstønadXMLMapper extends AbstractXMLMapper {

    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V2, singletonList(ENGANGSSØKNAD));

    private static final Logger LOG = LoggerFactory.getLogger(V2EngangsstønadXMLMapper.class);

    public V2EngangsstønadXMLMapper(Oppslag oppslag) {
        this(oppslag, new XMLStreamSøknadInspektør());
    }

    @Inject
    public V2EngangsstønadXMLMapper(Oppslag oppslag, SøknadInspektør inspektør) {
        super(oppslag, inspektør);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return EGENSKAPER;
    }

    @Override
    public Søknad tilSøknad(String xml) {
        // TODO
        LOG.info("Mapper ikke engangssøknad V2 tilbake til søknad foreløpig");
        return null;
    }

}
