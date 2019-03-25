package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;

public final class V1VarselJAXBUtil extends AbstractJAXBUtil {

    public V1VarselJAXBUtil() {
        this(false, false);
    }

    public V1VarselJAXBUtil(boolean validate) {
        this(validate, validate);

    }

    public V1VarselJAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(Varsel.class, ObjectFactory.class),
                validateMarshalling, validateUnmarshalling,
                "/varseltjeneste/xsd/varsel-v1.xsd");
    }
}
