package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.vedtak.felles.xml.vedtak.v2.ObjectFactory;
import no.nav.vedtak.felles.xml.vedtak.v2.Vedtak;

public final class VedtakV2JAXBUtil extends AbstractJAXBUtil {

    public VedtakV2JAXBUtil() {
        this(false, false);
    }

    public VedtakV2JAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(Vedtak.class, ObjectFactory.class),
                validateMarshalling, validateUnmarshalling,
                "/behandlingsprosess-vedtak-v2/xsd/vedtak-v2.xsd");
    }
}
