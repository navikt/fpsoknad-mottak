package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Inntekt extends TidsAvgrensetBrukerInfo {

    private double amount;

    @JsonCreator
    public Inntekt(@JsonProperty("type") String register,
            @JsonProperty("from") LocalDate from, @JsonProperty("to") Optional<LocalDate> to,
            @JsonProperty("amount") double amount) {
        super(register, "OK", from, to);
        this.amount = amount;
    }

    public double amount() {
        return amount;
    }
}
