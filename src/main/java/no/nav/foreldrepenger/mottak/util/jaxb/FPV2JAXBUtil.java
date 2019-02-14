package no.nav.foreldrepenger.mottak.util.jaxb;

public final class FPV2JAXBUtil extends AbstractJAXBUtil {

    public FPV2JAXBUtil() {
        this(false, false);
    }

    public FPV2JAXBUtil(boolean validateMarshalling, boolean validateUnmarshalling) {
        super(contextFra(
                no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.Endringssoeknad.class,
                no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.Foreldrepenger.class,
                no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.soeknad.v2.Soeknad.class),
                validateMarshalling, validateUnmarshalling,
                "/soeknad-v2/xsd/foreldrepenger/foreldrepenger-v2.xsd",
                "/soeknad-v2/xsd/endringssoeknad/endringssoeknad-v2.xsd",
                "/soeknad-v2/xsd/soeknad-v2.xsd");
    }

}
