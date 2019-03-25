package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.foreldrepenger.mottak.util.jaxb.VedtakV1ESJAXBUtil;

@Component
public class V1EngangsstønadXMLVedtakMapper implements XMLVedtakMapper {

    private static final Logger LOG = LoggerFactory.getLogger(V1EngangsstønadXMLVedtakMapper.class);

    private static final VedtakV1ESJAXBUtil JAXB = new VedtakV1ESJAXBUtil(false);

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return new MapperEgenskaper(Versjon.V1, SøknadType.INITIELL_ENGANGSSTØNAD);
    }

    @Override
    public Vedtak tilVedtak(String xml, SøknadEgenskap egenskap) {
        return Optional.ofNullable(xml)
                .map(x -> tilVedtak(x))
                .orElse(null);
    }

    private static Vedtak tilVedtak(String xml) {
        try {
            no.nav.vedtak.felles.xml.vedtak.v1.Vedtak vedtak = unmarshal(xml);
            return new Vedtak(null);
        } catch (Exception e) {
            LOG.warn("Feil ved unmarshalling av vedtak", e);
            return null;

        }
    }

    private static no.nav.vedtak.felles.xml.vedtak.v1.Vedtak unmarshal(String xml) {
        return JAXB.unmarshal(xml, no.nav.vedtak.felles.xml.vedtak.v1.Vedtak.class);
    }

}
