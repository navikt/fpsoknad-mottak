package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v2.Engangsstønad;
import no.nav.vedtak.felles.xml.soeknad.v2.Soeknad;

public final class ESV2JAXBUtil extends AbstractJAXBUtil {

    public ESV2JAXBUtil() {
        this(false, false);
    }

    public ESV2JAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(Soeknad.class, Engangsstønad.class),
                validateMarshalling, validateUnmarshalling,
                "/soeknad-v2/xsd/engangsstoenad/engangsstoenad-v2.xsd");
    }
}
