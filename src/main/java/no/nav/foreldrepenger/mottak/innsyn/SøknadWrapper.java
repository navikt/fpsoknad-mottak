package no.nav.foreldrepenger.mottak.innsyn;

import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import lombok.Data;

@Data
public class SøknadWrapper {
    private final String xml;
    private final String journalpostId;

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [xml=" + unescapeHtml4(xml) + ", journalpostId=" + journalpostId + "]";
    }
}
