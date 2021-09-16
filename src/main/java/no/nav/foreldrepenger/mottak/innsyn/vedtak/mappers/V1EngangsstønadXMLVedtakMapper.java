package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.util.Versjon.V1;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.util.jaxb.VedtakV1ESJAXBUtil;

@Component
public class V1EngangsstønadXMLVedtakMapper implements XMLVedtakMapper {
    private static final Logger LOG = LoggerFactory.getLogger(V1EngangsstønadXMLVedtakMapper.class);
    private static final VedtakV1ESJAXBUtil JAXB = new VedtakV1ESJAXBUtil(true);

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return new MapperEgenskaper(V1, INITIELL_ENGANGSSTØNAD);
    }

    @Override
    public Vedtak tilVedtak(String xml, SøknadEgenskap egenskap) {
        return Optional.ofNullable(xml)
                .map(V1EngangsstønadXMLVedtakMapper::tilVedtak)
                .orElse(null);
    }

    private static Vedtak tilVedtak(String xml) {
        try {
            unmarshal(xml);
            return new Vedtak(null, null);
        } catch (Exception e) {
            LOG.warn("Feil ved unmarshalling av vedtak fra {}", xml, e);
            return null;
        }
    }

    private static no.nav.vedtak.felles.xml.vedtak.v1.Vedtak unmarshal(String xml) {
        return JAXB.unmarshalToElement(xml, no.nav.vedtak.felles.xml.vedtak.v1.Vedtak.class).getValue();
    }
}
