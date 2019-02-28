package no.nav.foreldrepenger.mottak.innsyn.dto;

import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VedtakDTO {

    private final String xmlClob;
    private final String journalpostId;

    @JsonCreator
    public VedtakDTO(@JsonProperty("xmlClob") String xmlClob, @JsonProperty("journalpostId") String journalpostId) {
        this.xmlClob = xmlClob;
        this.journalpostId = journalpostId;
    }

    public String getXml() {
        return unescapeHtml4(xmlClob);
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xmlClob, journalpostId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        VedtakDTO other = (VedtakDTO) obj;
        if (journalpostId == null) {
            if (other.journalpostId != null) {
                return false;
            }
        }
        else if (!journalpostId.equals(other.journalpostId)) {
            return false;
        }
        if (xmlClob == null) {
            if (other.xmlClob != null) {
                return false;
            }
        }
        else if (!xmlClob.equals(other.xmlClob)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [xml=" + getXml().substring(0, 50) + ", journalpostId=" + journalpostId
                + "]";
    }
}
