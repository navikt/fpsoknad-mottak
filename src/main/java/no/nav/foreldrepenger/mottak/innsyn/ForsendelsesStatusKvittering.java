package no.nav.foreldrepenger.mottak.innsyn;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ForsendelsesStatusKvittering(@JsonProperty("forsendelseStatus") ForsendelseStatus forsendelseStatus) {
    static final ForsendelsesStatusKvittering PÅGÅR = new ForsendelsesStatusKvittering(ForsendelseStatus.PÅGÅR);

}
