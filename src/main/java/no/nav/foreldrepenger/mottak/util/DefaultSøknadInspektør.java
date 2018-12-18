package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENGANGSSØKNAD;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;

@Component
public final class DefaultSøknadInspektør implements SøknadInspektør {

    @Override
    public SøknadInspeksjonResultat inspiser(String xml) {
        return new SøknadInspeksjonResultat(typeFra(xml), versjonFra(xml));
    }

    @Override
    public Versjon versjon(String xml) {
        return inspiser(xml).versjon();
    }

    @Override
    public SøknadType type(String xml) {
        return inspiser(xml).type();
    }

    private static Versjon versjonFra(String xml) {
        try {
            XMLStreamReader reader = XMLInputFactory.newInstance()
                    .createXMLStreamReader(new StreamSource(new StringReader(xml)));
            while (!reader.isStartElement()) {
                reader.next();
            }
            return Versjon.fraNamespace(reader.getNamespaceURI());
        } catch (XMLStreamException e) {
            throw new IllegalStateException(e);
        }
    }

    private SøknadType typeFra(String xml) {
        try {

            String unescapedXML = unescapeHtml4(xml);
            if (unescapedXML.contains("soeknadsskjemaEngangsstoenad")) {
                return ENGANGSSØKNAD;
            }
            int ix = unescapedXML.indexOf("omYtelse>") + 1;
            String shortxml = unescapedXML.substring(ix + "omYtelse>".length());
            int begin = shortxml.indexOf("<");
            int end = shortxml.indexOf(">");
            String value = shortxml.substring(begin + 1, end);
            SøknadType type = value.contains("endringssoeknad") ? ENDRING : INITIELL;
            return type;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
