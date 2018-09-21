package no.nav.foreldrepenger.mottak.pdf;

import java.time.LocalDate;
import java.util.Objects;

public class Arbeidsforhold {

    private String arbeidsgiverNavn;
    private LocalDate from;
    private LocalDate to;
    private double stillingsprosent;

    public Arbeidsforhold(String arbeidsgiverNavn, LocalDate from, LocalDate to, double stillingsprosent) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
        this.from = from;
        this.to = to;
        this.stillingsprosent = stillingsprosent;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public double getStillingsprosent() {
        return stillingsprosent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeidsforhold that = (Arbeidsforhold) o;
        return Double.compare(that.stillingsprosent, stillingsprosent) == 0 &&
            Objects.equals(arbeidsgiverNavn, that.arbeidsgiverNavn) &&
            Objects.equals(from, that.from) &&
            Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiverNavn, from, to, stillingsprosent);
    }

    @Override
    public String toString() {
        return "Arbeidsforhold{" +
            "arbeidsgiverNavn='" + arbeidsgiverNavn + '\'' +
            ", from=" + from +
            ", to=" + to +
            ", stillingsprosent=" + stillingsprosent +
            '}';
    }
}
