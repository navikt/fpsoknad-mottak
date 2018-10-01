package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "forsendelseStatus")
@JsonSubTypes({
        @Type(value = FPFordelPendingKvittering.class, name = FPFordelPendingKvittering.STATUS),
        @Type(value = FPFordelGosysKvittering.class, name = FPFordelGosysKvittering.STATUS),
        @Type(value = FPSakFordeltKvittering.class, name = FPSakFordeltKvittering.STATUS)
})
public abstract class FPFordelKvittering {

    private final String forsendelseStatus;

    @JsonIgnore
    public String getforsendelseStatus() {
        return forsendelseStatus;
    }

    public FPFordelKvittering(String forsendelseStatus) {
        this.forsendelseStatus = forsendelseStatus;
    }
}
