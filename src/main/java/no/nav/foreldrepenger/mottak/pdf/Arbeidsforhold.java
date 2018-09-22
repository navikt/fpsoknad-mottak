package no.nav.foreldrepenger.mottak.pdf;

import java.time.LocalDate;
import java.util.Objects;

public class Arbeidsforhold {
    private String arbeidsgiverId;
    private String arbeidsgiverIdType;
    private LocalDate from;
    private LocalDate to;
    private Double stillingsprosent;
    private String arbeidsgiverNavn;

    public Arbeidsforhold(String arbeidsgiverId, String arbeidsgiverIdType, LocalDate from,
                          LocalDate to, Double stillingsprosent, String arbeidsgiverNavn) {
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

    public LocalDate getTo() {
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
