package no.nav.foreldrepenger.mottak.innsyn;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static no.nav.foreldrepenger.common.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.DOKMOT_ES_V1;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.UKJENT;
import static no.nav.foreldrepenger.common.util.Versjon.erEngangsstønadV1Dokmot;
import static no.nav.foreldrepenger.mottak.AbstractInspektør.SØKNAD;

import java.util.List;
import java.util.Optional;

import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.innsending.SøknadType;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.util.Versjon;
import no.nav.foreldrepenger.mottak.AbstractInspektør;

@Component
@Qualifier(SØKNAD)
public final class XMLStreamSøknadInspektør extends AbstractInspektør {
    private static final String ENGANGSSOEKNAD = "engangsstønad";
    private static final String ENDRINGSSOEKNAD = "endringssoeknad";
    private static final String FORELDREPENGER = "foreldrepenger";
    private static final String SVANGERSKAPSPENGER = "svangerskapspenger";
    private static final List<String> KJENTE_TAGS = List.of(FORELDREPENGER, ENDRINGSSOEKNAD, ENGANGSSOEKNAD,
            SVANGERSKAPSPENGER);
    private static final String OMYTELSE = "omYtelse";
    private static final Logger LOG = LoggerFactory.getLogger(XMLStreamSøknadInspektør.class);

    public XMLStreamSøknadInspektør() {
        super();
    }

    @Override
    public SøknadEgenskap inspiser(String xml) {
        String rootElementNamespace = rootElementNamespace(xml);
        return erEngangsstønadV1Dokmot(rootElementNamespace) ? DOKMOT_ES_V1 : egenskapFra(xml, rootElementNamespace);
    }

    private static SøknadEgenskap egenskapFra(String xml, String rootElementNamespace) {
        if (xml == null) {
            return UKJENT;
        }
        try {
            var reader = reader(xml);
            while (reader.hasNext()) {
                reader.next();
                if (reader.getEventType() == START_ELEMENT) {
                    if (reader.getAttributeCount() > 0 && reader.getLocalName().equals(OMYTELSE)) {
                        String type = reader.getAttributeValue(reader.getAttributeName(0).getNamespaceURI(),
                                "type");
                        if (type != null) {
                            if (type.toLowerCase().contains(FORELDREPENGER.toLowerCase())) {
                                LOG.debug("Fant type INITIELL fra attributt på OMYTELSE");
                                return new SøknadEgenskap(
                                        Versjon.namespaceFra(reader.getAttributeName(0).getNamespaceURI()),
                                        INITIELL_FORELDREPENGER);
                            }
                            if (type.toLowerCase().contains(ENDRINGSSOEKNAD.toLowerCase())) {
                                LOG.debug("Fant type ENDRING fra attributt på OMYTELSE");
                                return new SøknadEgenskap(
                                        Versjon.namespaceFra(reader.getAttributeName(0).getNamespaceURI()),
                                        ENDRING_FORELDREPENGER);
                            }
                            if (type.toLowerCase().contains(SVANGERSKAPSPENGER.toLowerCase())) {
                                LOG.debug("Fant type SVANGERSKAPSPENGER fra attributt på OMYTELSE");
                                return new SøknadEgenskap(
                                        Versjon.namespaceFra(reader.getAttributeName(0).getNamespaceURI()),
                                        SøknadType.INITIELL_SVANGERSKAPSPENGER);
                            }
                        }
                    }
                    if (reader.getLocalName().equalsIgnoreCase(FORELDREPENGER)) {
                        return new SøknadEgenskap(
                                Versjon.namespaceFra(reader.getNamespaceURI()),
                                INITIELL_FORELDREPENGER);
                    }
                    if (reader.getLocalName().equalsIgnoreCase(ENDRINGSSOEKNAD)) {
                        return new SøknadEgenskap(
                                Versjon.namespaceFra(namespace(reader, rootElementNamespace)),
                                ENDRING_FORELDREPENGER);
                    }
                    if (reader.getLocalName().equalsIgnoreCase(ENGANGSSOEKNAD)) {
                        return new SøknadEgenskap(
                                Versjon.namespaceFra(namespace(reader, rootElementNamespace)),
                                INITIELL_ENGANGSSTØNAD);
                    }
                    if (reader.getLocalName().equalsIgnoreCase(SVANGERSKAPSPENGER)) {
                        return new SøknadEgenskap(
                                Versjon.namespaceFra(namespace(reader, rootElementNamespace)),
                                INITIELL_SVANGERSKAPSPENGER);
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

    private static String namespace(XMLStreamReader reader, String rootElementNamespace) {
        return Optional.ofNullable(reader.getNamespaceURI())
                .orElse(rootElementNamespace);
    }
}
