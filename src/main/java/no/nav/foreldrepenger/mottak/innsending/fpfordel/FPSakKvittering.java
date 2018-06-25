package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FPSakKvittering {

    private final FPSakStatus forsendelseStatus;

    private final String saksnummer;

    private final String journalpostId;

    @JsonCreator
    public FPSakKvittering(@JsonProperty("forsendelseStatus") FPSakStatus forsendelseStatus,
            @JsonProperty("journalpostId") String journalpostId,
            @JsonProperty("saksnummer") String saksnummer) {
        this.forsendelseStatus = forsendelseStatus;
        this.journalpostId = journalpostId;
        this.saksnummer = saksnummer;
    }

}
