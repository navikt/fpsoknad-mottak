package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENGANGSSØKNAD;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;

import java.io.StringReader;

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
public final class DefaultSøknadInspektør implements SøknadInspektør {

    private static final String ENDRINGSSOEKNAD = "endringssoeknad";
    private static final String FORELDREPENGER = "foreldrepenger";
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSøknadInspektør.class);

    @Override
    public SøknadInspeksjonResultat inspiser(String xml) {
        return new SøknadInspeksjonResultat(typeFra(xml), versjonFra(xml));
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
            throw new UnsupportedVersionException(e);
        }
    }

    private static SøknadType fpTypeFra(String xml) {
        try {
            XMLStreamReader reader = XMLInputFactory.newInstance()
                    .createXMLStreamReader(new StreamSource(new StringReader(xml)));
            while (reader.hasNext()) {
                reader.next();
                if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
                    if (reader.getLocalName().equals(FORELDREPENGER)) {
                        return INITIELL;
                    }
                    if (reader.getLocalName().equals(ENDRINGSSOEKNAD)) {
                        return ENDRING;
                    }
                }
            }
            LOG.warn("Fant ingen kjente tags i søknaden, ukjent type");
            return SøknadType.UKJENT;
        } catch (Exception e) {
            LOG.warn("Noe gikk galt ved søk etter kjente tags i søknaden, ukjent type", e);
            return SøknadType.UKJENT;
        }
    }

}
