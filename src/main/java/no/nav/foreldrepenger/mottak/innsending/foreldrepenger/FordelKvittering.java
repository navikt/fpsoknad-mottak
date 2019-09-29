package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "forsendelseStatus")
@JsonSubTypes({
        @Type(value = PendingKvittering.class, name = PendingKvittering.STATUS),
        @Type(value = GosysKvittering.class, name = GosysKvittering.STATUS),
        @Type(value = FPSakFordeltKvittering.class, name = FPSakFordeltKvittering.STATUS)
})
public abstract class FordelKvittering {

    private final String forsendelseStatus;

    @JsonIgnore
    public String getforsendelseStatus() {
        return forsendelseStatus;
    }

    public FordelKvittering(String forsendelseStatus) {
        this.forsendelseStatus = forsendelseStatus;
    }
}
