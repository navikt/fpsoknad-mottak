package no.nav.foreldrepenger.mottak.innsyn.dto;

import static no.nav.foreldrepenger.common.util.StringUtil.limit;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SøknadDTO {
    private final String xml;
    private final String journalpostId;

    @JsonCreator
    public SøknadDTO(@JsonProperty("xml") String xml, @JsonProperty("journalpostId") String journalpostId) {
        this.xml = xml;
        this.journalpostId = journalpostId;
    }

    public String getXml() {
        return unescapeHtml4(xml);
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xml, journalpostId);
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
        SøknadDTO other = (SøknadDTO) obj;
        if (journalpostId == null) {
            if (other.journalpostId != null) {
                return false;
            }
        } else if (!journalpostId.equals(other.journalpostId)) {
            return false;
        }
        if (xml == null) {
            if (other.xml != null) {
                return false;
            }
        } else if (!xml.equals(other.xml)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [xml=" + limit(getXml()) + ", journalpostId=" + journalpostId + "]";
    }
}
