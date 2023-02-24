package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;

import java.time.LocalDate;
import java.util.Optional;

public record EnkeltArbeidsforhold(String arbeidsgiverId,
                                   String arbeidsgiverIdType,
                                   LocalDate from,
                                   Optional<LocalDate> to,
                                   ProsentAndel stillingsprosent,
                                   String arbeidsgiverNavn) {

    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private String arbeidsgiverId;
        private String arbeidsgiverIdType;
        private LocalDate from;
        private Optional<LocalDate> to;
        private ProsentAndel stillingsprosent;
        private String arbeidsgiverNavn;

        Builder() {
        }

        public Builder arbeidsgiverId(String arbeidsgiverId) {
            this.arbeidsgiverId = arbeidsgiverId;
            return this;
        }

        public Builder arbeidsgiverIdType(String arbeidsgiverIdType) {
            this.arbeidsgiverIdType = arbeidsgiverIdType;
            return this;
        }

        public Builder from(LocalDate from) {
            this.from = from;
            return this;
        }

        public Builder to(Optional<LocalDate> to) {
            this.to = to;
            return this;
        }

        public Builder stillingsprosent(ProsentAndel stillingsprosent) {
            this.stillingsprosent = stillingsprosent;
            return this;
        }

        public Builder arbeidsgiverNavn(String arbeidsgiverNavn) {
            this.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        public EnkeltArbeidsforhold build() {
            return new EnkeltArbeidsforhold(this.arbeidsgiverId, this.arbeidsgiverIdType, this.from,
                this.to, this.stillingsprosent, this.arbeidsgiverNavn);
        }
    }
}
