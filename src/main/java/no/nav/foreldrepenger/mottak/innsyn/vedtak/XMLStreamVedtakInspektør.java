package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static no.nav.foreldrepenger.mottak.AbstractInspektør.VEDTAK;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.UKJENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.AbstractInspektør;
import no.nav.foreldrepenger.common.innsending.SøknadType;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.util.Versjon;

@Component
@Qualifier(VEDTAK)
public final class XMLStreamVedtakInspektør extends AbstractInspektør {

    private static final String FAGSAKTYPE = "fagsakType";
    private static final String ENGANGSSTØNAD = "ENGANGSSTOENAD";
    private static final String FORELDREPENGER = "FORELDREPENGER";
    private static final String SVANGERSKAPSPENGER = "SVANGERSKAPSPENGER";

    private static final Logger LOG = LoggerFactory.getLogger(XMLStreamVedtakInspektør.class);

    @Override
    public SøknadEgenskap inspiser(String xml) {
        return egenskapFra(xml);
    }

    private static SøknadEgenskap egenskapFra(String xml) {
        if (xml == null) {
            return UKJENT;
        }
        try {
            String rootElementNamespace = rootElementNamespace(xml);
            var reader = reader(xml);
            while (reader.hasNext()) {
                reader.next();
                if (reader.getEventType() == START_ELEMENT) {
                    if (reader.getLocalName().toLowerCase().contains(FAGSAKTYPE.toLowerCase())) {
                        return new SøknadEgenskap(
                                Versjon.namespaceFra(rootElementNamespace),
                                typeFra(reader.getElementText()));
                    }
                }
            }
            LOG.warn("Fant ikke element {} i vedtak", FAGSAKTYPE);
            return UKJENT;
        } catch (Exception e) {
            LOG.warn("Fant ikke element {} i vedtak", FAGSAKTYPE, e);
            return UKJENT;
        }
    }

    private static SøknadType typeFra(String type) {
        return switch (type) {
            case ENGANGSSTØNAD -> INITIELL_ENGANGSSTØNAD;
            case SVANGERSKAPSPENGER -> INITIELL_SVANGERSKAPSPENGER;
            case FORELDREPENGER -> INITIELL_FORELDREPENGER;
            default -> {
                LOG.warn("Ukjent fagsaktype {}", type);
                yield SøknadType.UKJENT;
            }
        };
    }
}
