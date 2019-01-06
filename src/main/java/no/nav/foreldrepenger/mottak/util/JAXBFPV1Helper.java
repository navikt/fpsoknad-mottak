package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.Endringssoeknad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

public final class JAXBFPV1Helper extends AbstractJaxb {

    private static final Versjon VERSJON = V1;

    public JAXBFPV1Helper() {
        super(contextFra(
                Endringssoeknad.class,
                Foreldrepenger.class,
                no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.ObjectFactory.class,
                no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v1.ObjectFactory.class,
                Soeknad.class),
                VERSJON,
                "/foreldrepenger/foreldrepenger-v1.xsd",
                "/endringssoeknad-v1.xsd",
                "/soeknad-v1.xsd");
    }
}
