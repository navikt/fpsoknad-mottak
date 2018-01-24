package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;
import java.time.LocalDate;

import lombok.Data;

@Data
public class Varighet {

    private final LocalDate from;
    private final LocalDate to;

    @ConstructorProperties({ "from", "to" })
    public Varighet(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

}
