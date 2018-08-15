package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FPInfoKvittering {

    private final FPInfoForsendelsesStatus forsendelseStatus;

    @JsonCreator
    public FPInfoKvittering(@JsonProperty("forsendelseStatus") FPInfoForsendelsesStatus forsendelseStatus) {
        this.forsendelseStatus = forsendelseStatus;
    }
}
