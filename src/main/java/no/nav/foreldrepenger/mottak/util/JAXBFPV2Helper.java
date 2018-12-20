package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.util.Versjon.V2;

import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Component
public final class JAXBFPV2Helper extends AbstractJaxb {

    private static final Logger LOG = LoggerFactory.getLogger(JAXBFPV2Helper.class);
    private static final Versjon VERSJON = V2;

    public JAXBFPV2Helper() {
        super(contextFra(
                no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.Endringssoeknad.class,
                no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.Foreldrepenger.class,
                no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.soeknad.v2.Soeknad.class),
                schema());
    }

    @Override
    public Versjon versjon() {
        return VERSJON;
    }

    private static Schema schema() {
        try {
            return SCHEMA_FACTORY.newSchema(sourcesFra(VERSJON,
                    "/foreldrepenger/foreldrepenger-v2.xsd",
                    "/endringssoeknad-v2.xsd",
                    "/soeknad-v2.xsd"));
        } catch (SAXException e) {
            LOG.warn("Noe gikk galt med konfigurasjon av validering, bruker ikke-validerende marshaller");
            return null;
        }
    }

}
