package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;

public final class VarselV1JAXBUtil extends AbstractJAXBUtil {

    public VarselV1JAXBUtil() {
        this(false, false);
    }

    public VarselV1JAXBUtil(boolean validate) {
        this(validate, validate);

    }

    public VarselV1JAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(Varsel.class, ObjectFactory.class),
                validateMarshalling, validateUnmarshalling,
                "/varseltjeneste/xsd/varsel-v1.xsd");
    }
}
