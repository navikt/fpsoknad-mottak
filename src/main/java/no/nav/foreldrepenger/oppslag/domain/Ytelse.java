package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.oppslag.Register;

public class Ytelse extends TidsAvgrensetBrukerInfo {

    public Ytelse(Register register, String status, LocalDate from) {
        this(register, status, from, Optional.empty());
    }

    public Ytelse(Register register, String status, LocalDate from, Optional<LocalDate> to) {
        this(register.getDisplayValue(), status, from, to);
    }

    @JsonCreator
    public Ytelse(@JsonProperty("type") String register, @JsonProperty("status") String status,
            @JsonProperty("from") LocalDate from, @JsonProperty("to") Optional<LocalDate> to) {
        super(register, status, from, to);
    }

}
