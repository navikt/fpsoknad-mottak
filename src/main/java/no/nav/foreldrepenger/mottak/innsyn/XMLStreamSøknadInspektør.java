package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Arrays.asList;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.UKJENT;

import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.AbstractInspektør;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public final class XMLStreamSøknadInspektør extends AbstractInspektør implements SøknadInspektør {

    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();
    private static final String ENGANGSSOEKNAD = "engangsstønad";
    private static final String ENDRINGSSOEKNAD = "endringssoeknad";
    private static final String FORELDREPENGER = "foreldrepenger";
    private static final String SVANGERSKAPSPENGER = "svangerskapspenger";

    private static final List<String> KJENTE_TAGS = asList(FORELDREPENGER, ENDRINGSSOEKNAD, ENGANGSSOEKNAD,
            SVANGERSKAPSPENGER);

    private static final String OMYTELSE = "omYtelse";

    private static final Logger LOG = LoggerFactory.getLogger(XMLStreamSøknadInspektør.class);

    @Override
    public SøknadEgenskap inspiser(String xml) {
        String namespace = namespaceFra(xml);
        SøknadEgenskap egenskap = new SøknadEgenskap(versjonFraXML(xml), typeFra(xml, namespace));
        if (egenskap.erUkjent()) {
            LOG.warn("Søknad {} kunne ikke analyseres", xml);
            return SøknadEgenskap.UKJENT;
        }
        LOG.info("Søknad har egenskap {}", egenskap);
        return egenskap;
    }

    private static SøknadType typeFra(String xml, String namespace) {
        if (Versjon.erEngangsstønadV1Dokmot(namespace)) {
            return INITIELL_ENGANGSSTØNAD;
        }
        return fpTypeFra(xml);
    }

    private static SøknadType fpTypeFra(String xml) {
        if (xml == null) {
            return UKJENT;
        }
        try {
            XMLStreamReader reader = reader(xml);
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
                                if (type.toLowerCase().contains(SVANGERSKAPSPENGER.toLowerCase())) {
                                    LOG.debug("Fant type SVANGERSKAPSPENGER fra attributt på OMYTELSE");
                                    return SøknadType.INITIELL_SVANGERSKAPSPENGER;
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
                    if (reader.getLocalName().equalsIgnoreCase(SVANGERSKAPSPENGER)) {
                        return INITIELL_SVANGERSKAPSPENGER;
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

}
