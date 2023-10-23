package no.nav.foreldrepenger.mottak.oppslag.pdl;

public enum Ytelse {
        ENGANGSSTØNAD("B321"),
        FORELDREPENGER("B271"),
        SVANGERSKAPSPENGER("B322");

        private final String behandlingsnummer;

        Ytelse(String behandlingsnummer) {
            this.behandlingsnummer = behandlingsnummer;
        }

        public String getBehandlingsnummer() {
            return behandlingsnummer;
        }
    }
