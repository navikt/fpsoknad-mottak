package no.nav.foreldrepenger.mottak.innsending;

public enum SøknadType {
    INITIELL_FORELDREPENGER, ETTERSENDING_FORELDREPENGER, ENDRING_FORELDREPENGER, INITIELL_ENGANGSSTØNAD, ETTERSENDING_ENGANGSSTØNAD, INITIELL_ENGANGSSTØNAD_DOKMOT, UKJENT;

    public boolean erForeldrepenger() {
        return this.equals(INITIELL_FORELDREPENGER)
                || this.equals(ENDRING_FORELDREPENGER)
                || this.equals(ETTERSENDING_FORELDREPENGER);
    }

    public boolean erEngangsstønad() {
        return this.equals(INITIELL_ENGANGSSTØNAD)
                || this.equals(ETTERSENDING_ENGANGSSTØNAD)
                || this.equals(INITIELL_ENGANGSSTØNAD_DOKMOT);
    }
}
