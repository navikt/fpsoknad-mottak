package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;

public final class ESV1JAXBUtil extends AbstractJAXBUtil {

    private static final Versjon VERSJON = V1;

    public ESV1JAXBUtil() {
        this(false, false);
    }

    public ESV1JAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(SoeknadsskjemaEngangsstoenad.class, Dokumentforsendelse.class),
                VERSJON,
                validateMarshalling, validateUnmarshalling,
                "/engangsstoenad/konvolutt-dokmot-v1.xsd",
                "/engangsstoenad/engangsstoenad-dokmot-v1.xsd");
    }
}
