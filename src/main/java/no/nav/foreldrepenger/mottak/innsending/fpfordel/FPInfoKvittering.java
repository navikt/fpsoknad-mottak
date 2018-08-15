package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FPInfoKvittering {

    private final FPSakStatus forsendelseStatus;

    @JsonCreator
    public FPInfoKvittering(@JsonProperty("forsendelseStatus") FPSakStatus forsendelseStatus) {
        this.forsendelseStatus = forsendelseStatus;
    }

}
