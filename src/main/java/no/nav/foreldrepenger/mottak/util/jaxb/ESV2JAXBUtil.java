package no.nav.foreldrepenger.mottak.util.jaxb;

import static no.nav.foreldrepenger.mottak.util.Versjon.V2;

import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v2.Engangsstønad;
import no.nav.vedtak.felles.xml.soeknad.v2.Soeknad;

public final class ESV2JAXBUtil extends AbstractJAXBUtil {

    private static final Versjon VERSJON = V2;

    public ESV2JAXBUtil() {
        this(false, false);
    }

    public ESV2JAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(Soeknad.class, Engangsstønad.class),
                VERSJON,
                validateMarshalling, validateUnmarshalling,
                "/engangsstoenad/engangsstoenad-v2.xsd");
    }
}
