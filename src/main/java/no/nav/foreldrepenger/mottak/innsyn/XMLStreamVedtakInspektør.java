package no.nav.foreldrepenger.mottak.innsyn;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public final class XMLStreamVedtakInspektør implements XMLVedtakInspektør {

    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

    private static final Logger LOG = LoggerFactory.getLogger(XMLStreamVedtakInspektør.class);

    @Override
    public Versjon inspiser(String xml) {
        return versjonFra(namespaceFra(xml));
    }

    private static Versjon versjonFra(String namespace) {
        return Versjon.namespaceFra(namespace);
    }

    private static String namespaceFra(String xml) {
        if (xml == null) {
            return null;
        }
        try {
            XMLStreamReader reader = createReader(xml);
            while (!reader.isStartElement()) {
                reader.next();
            }
            return reader.getNamespaceURI();
        } catch (Exception e) {
            LOG.warn("Kunne ikke hente namespace fra {}", xml);
            return null;
        }
    }

    private static XMLStreamReader createReader(String xml) throws XMLStreamException {
        return FACTORY.createXMLStreamReader(new StreamSource(new StringReader(xml)));
    }
}
