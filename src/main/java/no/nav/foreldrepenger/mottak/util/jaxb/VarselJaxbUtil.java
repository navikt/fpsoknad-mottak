package no.nav.foreldrepenger.mottak.util.jaxb;

import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;

public final class VarselJaxbUtil extends AbstractJAXBUtil {

    public VarselJaxbUtil() {
        this(false, false);
    }

    public VarselJaxbUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(Varsel.class), Versjon.V1,
            validateMarshalling, validateUnmarshalling,
            "/varseltjeneste/varsel-v1.xsd");
    }

}
