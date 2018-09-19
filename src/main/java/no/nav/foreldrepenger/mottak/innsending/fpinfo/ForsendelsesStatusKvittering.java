package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ForsendelsesStatusKvittering {

    private final ForsendelseStatus forsendelseStatus;

    @JsonCreator
    public ForsendelsesStatusKvittering(@JsonProperty("forsendelseStatus") ForsendelseStatus forsendelseStatus) {
        this.forsendelseStatus = forsendelseStatus;
    }
}
