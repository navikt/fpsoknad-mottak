package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

public final class ESV1JAXBUtil extends AbstractJAXBUtil {

    public ESV1JAXBUtil() {
        this(false, false);
    }

    public ESV1JAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(SoeknadsskjemaEngangsstoenad.class, Soeknad.class),
                validateMarshalling, validateUnmarshalling,
                "dokmot/v1/engangsstoenad-dokmot-v1.xsd");
    }
}
