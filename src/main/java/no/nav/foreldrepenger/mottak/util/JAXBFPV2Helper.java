package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.util.Versjon.V2;

public final class JAXBFPV2Helper extends AbstractJaxb {

    private static final Versjon VERSJON = V2;

    public JAXBFPV2Helper() {
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
