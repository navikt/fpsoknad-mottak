package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Arrays.asList;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.UKJENT;

import java.io.StringReader;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public final class XMLStreamSøknadInspektør implements SøknadInspektør {

    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();
    private static final String ENGANGSSOEKNAD = "engangsstønad";
    private static final String ENDRINGSSOEKNAD = "endringssoeknad";
    private static final String FORELDREPENGER = "foreldrepenger";
    private static final List<String> KJENTE_TAGS = asList(FORELDREPENGER, ENDRINGSSOEKNAD, ENGANGSSOEKNAD);

    private static final String OMYTELSE = "omYtelse";

    private static final Logger LOG = LoggerFactory.getLogger(XMLStreamSøknadInspektør.class);

    @Override
    public SøknadEgenskap inspiser(String xml) {
        String namespace = namespaceFra(xml);
        SøknadEgenskap egenskap = new SøknadEgenskap(versjonFra(namespace), typeFra(xml, namespace));
        if (egenskap.erUkjent()) {
            LOG.warn("Søknad {} kunne ikke analyseres", xml);
            return SøknadEgenskap.UKJENT;
        }
        LOG.info("Søknad har egenskap {}", egenskap);
        return egenskap;
    }

    private static SøknadType typeFra(String xml, String namespace) {
        return !erEngangsstønadV1Dokmot(namespace) ? fpTypeFra(xml) : INITIELL_ENGANGSSTØNAD;
    }

    private static boolean erEngangsstønadV1Dokmot(String namespace) {
        return namespace != null && namespace.startsWith("http");
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

    private static SøknadType fpTypeFra(String xml) {
        if (xml == null) {
            return UKJENT;
        }
        try {
            XMLStreamReader reader = createReader(xml);
            while (reader.hasNext()) {
                reader.next();
                if (reader.getEventType() == START_ELEMENT) {
                    if (reader.getLocalName().equals(OMYTELSE)) {
                        if (reader.getAttributeCount() > 0) {
                            String type = reader.getAttributeValue(reader.getAttributeName(0).getNamespaceURI(),
                                    "type");
                            if (type != null) {
                                if (type.toLowerCase().contains(FORELDREPENGER.toLowerCase())) {
                                    LOG.debug("Fant type INITIELL fra attributt på OMYTELSE");
                                    return INITIELL_FORELDREPENGER;
                                }
                                if (type.toLowerCase().contains(ENDRINGSSOEKNAD.toLowerCase())) {
                                    LOG.debug("Fant type ENDRING fra attributt på OMYTELSE");
                                    return ENDRING_FORELDREPENGER;
                                }
                            }
                        }
                    }
                    if (reader.getLocalName().equalsIgnoreCase(FORELDREPENGER)) {
                        return INITIELL_FORELDREPENGER;
                    }
                    if (reader.getLocalName().equalsIgnoreCase(ENDRINGSSOEKNAD)) {
                        return ENDRING_FORELDREPENGER;
                    }
                    if (reader.getLocalName().equalsIgnoreCase(ENGANGSSOEKNAD)) {
                        return INITIELL_ENGANGSSTØNAD;
                    }
                }
            }

            LOG.warn("Fant ingen av de kjente tags {} i søknaden, kan ikke fastslå type", KJENTE_TAGS);
            return UKJENT;
        } catch (Exception e) {
            LOG.warn("Feil ved søk etter kjente tags {} i {} , kan ikke fastslå type", KJENTE_TAGS, xml, e);
            return UKJENT;
        }
    }

    private static XMLStreamReader createReader(String xml) throws XMLStreamException {
        return FACTORY.createXMLStreamReader(new StreamSource(new StringReader(xml)));
    }
}
