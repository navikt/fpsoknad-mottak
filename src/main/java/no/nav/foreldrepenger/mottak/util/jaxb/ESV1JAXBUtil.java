package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;

public final class ESV1JAXBUtil extends AbstractJAXBUtil {

    public ESV1JAXBUtil() {
        this(false, false);
    }

    public ESV1JAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(SoeknadsskjemaEngangsstoenad.class, Dokumentforsendelse.class),
                validateMarshalling, validateUnmarshalling,
                "dokmot/v1/engangsstoenad-dokmot-v1.xsd");
    }
}
