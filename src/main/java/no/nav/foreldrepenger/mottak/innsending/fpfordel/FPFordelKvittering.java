package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "status")
@JsonSubTypes({
        @Type(value = FPFordelPendingKvittering.class, name = FPFordelPendingKvittering.STATUS),
        @Type(value = FPFordelManuellKvittering.class, name = FPFordelManuellKvittering.STATUS),
        @Type(value = FPSakFordeltKvittering.class, name = FPSakFordeltKvittering.STATUS)
})
public abstract class FPFordelKvittering {

    private final String status;

    @JsonIgnore
    public String getStatus() {
        return status;
    }

    public FPFordelKvittering(String status) {
        this.status = status;
    }
}
