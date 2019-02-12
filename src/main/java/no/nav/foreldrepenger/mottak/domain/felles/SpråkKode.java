package no.nav.foreldrepenger.mottak.domain.felles;

public enum SpråkKode {
    NN, NB, EN;

    public static SpråkKode defaultSpråk() {
        return NB;
    }
}
