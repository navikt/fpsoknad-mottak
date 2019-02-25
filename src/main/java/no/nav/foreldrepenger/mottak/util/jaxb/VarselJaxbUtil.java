package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;

public final class VarselJaxbUtil extends AbstractJAXBUtil {

    public VarselJaxbUtil() {
        this(false, false);
    }

    public VarselJaxbUtil(boolean validate) {
        this(validate, validate);

    }

    public VarselJaxbUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(Varsel.class, ObjectFactory.class),
                validateMarshalling, validateUnmarshalling,
                "/varseltjeneste/xsd/varsel-v1.xsd");
    }
}
