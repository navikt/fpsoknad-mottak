package no.nav.foreldrepenger.mottak.util;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Component
public final class JAXBFPV2Helper extends AbstractJaxb {

    private static final Logger LOG = LoggerFactory.getLogger(JAXBFPV2Helper.class);

    public JAXBFPV2Helper() {
        super(contextFra(
                no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.Endringssoeknad.class,
                no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.Foreldrepenger.class,
                no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.ObjectFactory.class,
                no.nav.vedtak.felles.xml.soeknad.v2.Soeknad.class),
                fpSchema(Versjon.V2));
    }

    @Override
    Versjon version() {
        return Versjon.V2;
    }

    private static Schema fpSchema(Versjon version) {
        try {
            return SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI)
                    .newSchema(sourcesFra(version,
                            "/foreldrepenger/foreldrepenger-v2.xsd",
                            "/endringssoeknad-v2.xsd",
                            "/soeknad-v2.xsd"));
        } catch (SAXException e) {
            LOG.warn("Noe gikk galt med konfigurasjon av validering, bruker ikke-validerende marshaller");
            return null;
        }
    }

}
