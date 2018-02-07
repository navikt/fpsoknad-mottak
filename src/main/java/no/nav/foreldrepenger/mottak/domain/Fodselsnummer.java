package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Fodselsnummer extends Bruker {

    @JsonCreator
    public Fodselsnummer(@JsonProperty("value") String value) {
        super(value);
    }

    public static Fodselsnummer valueOf(String value) {
        return new Fodselsnummer(value);
    }

}
