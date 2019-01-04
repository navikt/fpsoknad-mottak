package no.nav.foreldrepenger.mottak.innsyn.dto;

import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import lombok.Data;

@Data
public class SøknadDTO {
    private final String xml;
    private final String journalpostId;

    public String getXml() {
        return unescapeHtml4(xml);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [xml=" + getXml() + ", journalpostId=" + journalpostId + "]";
    }
}
