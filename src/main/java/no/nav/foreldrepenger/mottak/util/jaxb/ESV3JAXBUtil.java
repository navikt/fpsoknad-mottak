package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v3.Engangsstønad;
import no.nav.vedtak.felles.xml.soeknad.v3.Soeknad;

public final class ESV3JAXBUtil extends AbstractJAXBUtil {

    public ESV3JAXBUtil() {
        this(false, false);
    }

    public ESV3JAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(Soeknad.class, Engangsstønad.class),
                validateMarshalling, validateUnmarshalling,
                "/soeknad-v3/xsd/engangsstoenad/engangsstoenad-v3.xsd");
    }
}
