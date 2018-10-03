package no.nav.foreldrepenger.mottak.util;

public class DokumentTypeAnalysator {

    public boolean erEndringssøknad(String xml) {
        int ix = xml.indexOf("omYtelse>");
        String shortxml = xml.substring(ix + "omYtelse>".length());
        int begin = shortxml.indexOf("<");
        int end = shortxml.indexOf(">");
        String value = shortxml.substring(begin + 1, end);
        return value.equals("endringssoeknad");
    }
}
