package no.nav.foreldrepenger.mottak.util;

import static java.util.Arrays.asList;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENGANGSSØKNAD;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.UKJENT;

import java.io.StringReader;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.http.errorhandling.UnsupportedVersionException;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;

@Component
public final class XMLStreamSøknadInspektør implements SøknadInspektør {

    private static final String ENDRINGSSOEKNAD = "endringssoeknad";
    private static final String FORELDREPENGER = "foreldrepenger";
    private static final Logger LOG = LoggerFactory.getLogger(XMLStreamSøknadInspektør.class);

    @Override
    public SøknadEgenskaper inspiser(String xml) {
        return new SøknadEgenskaper(typeFra(xml), versjonFra(xml));
    }

    private SøknadType typeFra(String xml) {
        return erEngangsstønad(xml) ? ENGANGSSØKNAD : fpTypeFra(xml);
    }

    private boolean erEngangsstønad(String xml) {
        return namespaceFra(xml).startsWith("http");
    }

    private static Versjon versjonFra(String xml) {
        return Versjon.namespaceFra(namespaceFra(xml));
    }

    private static String namespaceFra(String xml) {
        try {
            XMLStreamReader reader = XMLInputFactory.newInstance()
                    .createXMLStreamReader(new StreamSource(new StringReader(xml)));
            while (!reader.isStartElement()) {
                reader.next();
            }
            return reader.getNamespaceURI();
        } catch (XMLStreamException e) {
            LOG.warn("Kunne ikke hente namespace fra XML");
            throw new UnsupportedVersionException(e);
        }
    }

    private static SøknadType fpTypeFra(String xml) {
        try {
            XMLStreamReader reader = XMLInputFactory.newInstance()
                    .createXMLStreamReader(new StreamSource(new StringReader(xml)));
            while (reader.hasNext()) {
                reader.next();
                if (reader.getEventType() == START_ELEMENT) {
                    if (reader.getLocalName().equals(FORELDREPENGER)) {
                        return INITIELL;
                    }
                    if (reader.getLocalName().equals(ENDRINGSSOEKNAD)) {
                        return ENDRING;
                    }
                }
            }
            LOG.warn("Fant ingen av de kjente tags {} i søknaden, kan ikke fastslå type", asList(INITIELL, ENDRING));
            return UKJENT;
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOG.warn("Feil ved søk etter kjente tags {} i søknaden {}, kan ikke fastslå type",
                    asList(INITIELL, ENDRING), xml, e);
            return UKJENT;
        }
    }

}
