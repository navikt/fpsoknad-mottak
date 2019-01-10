package no.nav.foreldrepenger.mottak.innsyn;

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
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public final class XMLStreamSøknadInspektør implements SøknadInspektør {

    private static final String ENDRINGSSOEKNAD = "endringssoeknad";
    private static final String FORELDREPENGER = "foreldrepenger";
    private static final String OMYTELSE = "omYtelse";

    private static final Logger LOG = LoggerFactory.getLogger(XMLStreamSøknadInspektør.class);

    @Override
    public SøknadEgenskaper inspiser(String xml) {
        SøknadEgenskaper egenskaper = new SøknadEgenskaper(typeFra(xml), versjonFra(xml));
        if (egenskaper.getType().equals(UKJENT)) {
            LOG.warn("Søknad er type {} og versjon {} {}", egenskaper.getType(), egenskaper.getVersjon(), xml);
        }
        else {
            LOG.info("Søknad er type {} og versjon {}", egenskaper.getType(), egenskaper.getVersjon());
        }
        return egenskaper;
    }

    private static SøknadType typeFra(String xml) {
        return !erEngangsstønad(xml) ? fpTypeFra(xml) : ENGANGSSØKNAD;
    }

    private static boolean erEngangsstønad(String xml) {
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
            LOG.warn("Kunne ikke hente namespace fra XML {}", xml);
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
                    if (reader.getLocalName().equals(OMYTELSE)) {
                        if (reader.getAttributeCount() > 0) {
                            String type = reader.getAttributeValue(reader.getAttributeName(0).getNamespaceURI(),
                                    "type");
                            if (type != null) {
                                if (type.toLowerCase().contains(FORELDREPENGER.toLowerCase())) {
                                    return INITIELL;
                                }
                                if (type.toLowerCase().contains(ENDRINGSSOEKNAD.toLowerCase())) {
                                    return ENDRING;
                                }
                            }
                        }
                        if (reader.getAttributeName(0).getLocalPart().equalsIgnoreCase(FORELDREPENGER)) {
                            LOG.warn("Fant type INITIELL fra attributt på OMYTELSE");
                            return INITIELL;
                        }
                        if (reader.getAttributeName(0).getLocalPart().equalsIgnoreCase(ENDRINGSSOEKNAD)) {
                            LOG.warn("Fant type ENDRING fra attributt på OMYTELSE");
                            return ENDRING;
                        }
                    }
                    if (reader.getLocalName().equalsIgnoreCase(FORELDREPENGER)) {
                        return INITIELL;
                    }
                    if (reader.getLocalName().equalsIgnoreCase(ENDRINGSSOEKNAD)) {
                        return ENDRING;
                    }
                }
            }

            LOG.warn("Fant ingen av de kjente tags {} i søknaden, kan ikke fastslå type",
                    asList(FORELDREPENGER, ENDRINGSSOEKNAD));
            return UKJENT;
        } catch (XMLStreamException | FactoryConfigurationError e) {
            LOG.warn("Feil ved søk etter kjente tags {} i søknaden {}, kan ikke fastslå type",
                    asList(FORELDREPENGER, ENDRINGSSOEKNAD), xml, e);
            return UKJENT;
        }
    }

}
