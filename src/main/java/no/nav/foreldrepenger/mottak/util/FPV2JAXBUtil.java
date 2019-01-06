package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.util.Versjon.V2;

public final class FPV2JAXBUtil extends AbstractJAXBUtil {

    private static final Versjon VERSJON = V2;

    public FPV2JAXBUtil() {
        super(contextFra(
                no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.Endringssoeknad.class,
                no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.Foreldrepenger.class,
                no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.soeknad.v2.Soeknad.class),
                VERSJON,
                "/foreldrepenger/foreldrepenger-v2.xsd",
                "/endringssoeknad-v2.xsd",
                "/soeknad-v2.xsd");
    }

}
