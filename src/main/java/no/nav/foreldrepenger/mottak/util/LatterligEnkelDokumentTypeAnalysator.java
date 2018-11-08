package no.nav.foreldrepenger.mottak.util;

import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

public final class LatterligEnkelDokumentTypeAnalysator {

    private LatterligEnkelDokumentTypeAnalysator() {

    }

    public static boolean erEndringssøknad(String xml) {
        String unescapedXML = unescapeHtml4(xml);
        int ix = unescapedXML.indexOf("omYtelse>") + 1;
        String shortxml = unescapedXML.substring(ix + "omYtels>".length());
        int begin = shortxml.indexOf("<");
        int end = shortxml.indexOf(">");
        String value = shortxml.substring(begin + 1, end);
        return value.contains("endringssoeknad");
    }
}
