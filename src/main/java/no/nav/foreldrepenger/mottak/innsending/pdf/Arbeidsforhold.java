package no.nav.foreldrepenger.mottak.innsending.pdf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Arbeidsforhold {
    private String arbeidsgiverId;
    private String arbeidsgiverIdType;
    private LocalDate from;
    private Optional<LocalDate> to;
    private Double stillingsprosent;
    private String arbeidsgiverNavn;

    @JsonCreator
    public Arbeidsforhold(@JsonProperty("arbeidsgiverId") String arbeidsgiverId,
                          @JsonProperty("arbeidsgiverIdType") String arbeidsgiverIdType,
                          @JsonProperty("from") LocalDate from,
                          @JsonProperty("to") Optional<LocalDate> to,
                          @JsonProperty("stillingsprosent") Double stillingsprosent,
                          @JsonProperty("arbeidsgiverNavn") String arbeidsgiverNavn) {
        this.arbeidsgiverId = arbeidsgiverId;
        this.arbeidsgiverIdType = arbeidsgiverIdType;
        this.from = from;
        this.to = to;
        this.stillingsprosent = stillingsprosent;
        this.arbeidsgiverNavn = arbeidsgiverNavn;
    }

    public String getArbeidsgiverId() {
        return arbeidsgiverId;
    }

    public String getArbeidsgiverIdType() {
        return arbeidsgiverIdType;
    }

    public LocalDate getFrom() {
        return from;
    }

    public Optional<LocalDate> getTo() {
        return to;
    }

    public Double getStillingsprosent() {
        return stillingsprosent;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeidsforhold that = (Arbeidsforhold) o;
        return Objects.equals(arbeidsgiverId, that.arbeidsgiverId) &&
            Objects.equals(arbeidsgiverIdType, that.arbeidsgiverIdType) &&
            Objects.equals(from, that.from) &&
            Objects.equals(to, that.to) &&
            Objects.equals(stillingsprosent, that.stillingsprosent) &&
            Objects.equals(arbeidsgiverNavn, that.arbeidsgiverNavn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiverId, arbeidsgiverIdType, from, to, stillingsprosent, arbeidsgiverNavn);
    }

    @Override
    public String toString() {
        return "Arbeidsforhold{" +
            "arbeidsgiverId='" + arbeidsgiverId + '\'' +
            ", arbeidsgiverIdType='" + arbeidsgiverIdType + '\'' +
            ", from=" + from +
            ", to=" + to +
            ", stillingsprosent=" + stillingsprosent +
            ", arbeidsgiverNavn='" + arbeidsgiverNavn + '\'' +
            '}';
    }
}
