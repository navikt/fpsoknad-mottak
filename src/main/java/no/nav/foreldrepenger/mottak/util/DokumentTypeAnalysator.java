package no.nav.foreldrepenger.mottak.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DokumentTypeAnalysator {

    private static final Logger LOG = LoggerFactory.getLogger(DokumentTypeAnalysator.class);

    public boolean erEndringssøknad(String xml) {
        int ix = xml.indexOf("omYtelse>") + 1;
        String shortxml = xml.substring(ix + "omYtels>".length());
        int begin = shortxml.indexOf("<");
        int end = shortxml.indexOf(">");
        String value = shortxml.substring(begin + 1, end);
        return value.equals("endringssoeknad");
    }
}
