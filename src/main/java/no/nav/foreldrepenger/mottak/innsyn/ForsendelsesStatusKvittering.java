package no.nav.foreldrepenger.mottak.innsyn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ForsendelsesStatusKvittering {

    static final ForsendelsesStatusKvittering PÅGÅR = new ForsendelsesStatusKvittering(ForsendelseStatus.PÅGÅR);

    private final ForsendelseStatus forsendelseStatus;

    @JsonCreator
    public ForsendelsesStatusKvittering(@JsonProperty("forsendelseStatus") ForsendelseStatus forsendelseStatus) {
        this.forsendelseStatus = forsendelseStatus;
    }
}
