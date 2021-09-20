package no.nav.foreldrepenger.mottak;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.common.util.Versjon;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;

public abstract class AbstractInspektør implements Inspektør {
    public static final String SØKNAD = "søknad";
    public static final String VEDTAK = "vedtak";
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();
    private static final Logger LOG = LoggerFactory.getLogger(AbstractInspektør.class);

    protected AbstractInspektør() {
    }

    protected static Versjon versjonFraXML(String xml) {
        return versjonFra(rootElementNamespace(xml));
    }

    private static Versjon versjonFra(String namespace) {
        return Versjon.namespaceFra(namespace);
    }

    protected static String rootElementNamespace(String xml) {
        if (xml == null) {
            return null;
        }
        try {
            XMLStreamReader reader = reader(xml);
            while (!reader.isStartElement()) {
                reader.next();
            }
            return reader.getNamespaceURI();
        } catch (Exception e) {
            LOG.warn("Kunne ikke hente namespace fra {}", xml);
            return null;
        }
    }

    protected static XMLStreamReader reader(String xml) throws XMLStreamException {
        return FACTORY.createXMLStreamReader(new StreamSource(new StringReader(xml)));
    }
}
