package no.nav.foreldrepenger.mottak.util.jaxb;

import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.Endringssoeknad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

public final class FPV1JAXBUtil extends AbstractJAXBUtil {

    private static final Versjon VERSJON = V1;

    public FPV1JAXBUtil() {
        this(false, false);
    }

    public FPV1JAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(
                Endringssoeknad.class,
                Foreldrepenger.class,
                no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.ObjectFactory.class,
                no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.ObjectFactory.class,
                Soeknad.class),
                VERSJON,
                validateMarshalling, validateUnmarshalling,
                "/foreldrepenger/foreldrepenger-v1.xsd",
                "/endringssoeknad-v1.xsd",
                "/soeknad-v1.xsd");
    }
}
