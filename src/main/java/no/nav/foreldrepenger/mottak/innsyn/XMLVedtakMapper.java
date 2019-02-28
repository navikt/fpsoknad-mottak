package no.nav.foreldrepenger.mottak.innsyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.util.jaxb.VedtakV2JAXBUtil;
import no.nav.vedtak.felles.xml.vedtak.v2.Uttak;

@Component
public class XMLVedtakMapper {

    private static final Logger LOG = LoggerFactory.getLogger(XMLVedtakMapper.class);

    private static final VedtakV2JAXBUtil JAXB = new VedtakV2JAXBUtil();

    public Vedtak tilVedtak(String xml) {
        if (xml == null) {
            return null;
        }
        try {
            no.nav.vedtak.felles.xml.vedtak.v2.Vedtak vedtak = JAXB.unmarshal(xml,
                    no.nav.vedtak.felles.xml.vedtak.v2.Vedtak.class);
            Uttak uttak = vedtak.getBehandlingsresultat().getBeregningsresultat().getUttak();
            LOG.info("Fikk uttak {}", uttak.getAny());
            return new Vedtak(xml);
        } catch (Exception e) {
            LOG.warn("Feil ved unmarshalling av vedtak", e);
            return null;

        }
    }

}
