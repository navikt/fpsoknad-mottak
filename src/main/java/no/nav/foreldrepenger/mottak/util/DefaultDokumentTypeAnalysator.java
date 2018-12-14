package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;

@Component
public final class DefaultDokumentTypeAnalysator implements DokumentAnalysator {

    @Override
    public AnalyseResultat analyser(String xml) {
        String unescapedXML = unescapeHtml4(xml);
        int ix = unescapedXML.indexOf("omYtelse>") + 1;
        String shortxml = unescapedXML.substring(ix + "omYtelse>".length());
        int begin = shortxml.indexOf("<");
        int end = shortxml.indexOf(">");
        String value = shortxml.substring(begin + 1, end);
        Versjon v = Versjon.fraNamespace(namespaceFra(xml));
        return value.contains("endringssoeknad") ? new AnalyseResultat(ENDRING, v)
                : new AnalyseResultat(INITIELL, v);
    }

    @Override
    public Versjon versjon(String xml) {
        return analyser(xml).getVersjon();
    }

    @Override
    public SøknadType type(String xml) {
        return analyser(xml).type();
    }

    public static String namespaceFra(Source xmlSource) {
        try {
            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(xmlSource);
            while (!xmlStreamReader.isStartElement()) {
                xmlStreamReader.next();
            }
            return xmlStreamReader.getNamespaceURI();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String namespaceFra(String xml) {
        try (final StringReader reader = new StringReader(xml)) {
            return namespaceFra(new StreamSource(reader));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
