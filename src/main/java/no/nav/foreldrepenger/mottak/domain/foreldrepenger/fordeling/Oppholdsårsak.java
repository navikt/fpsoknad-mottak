package no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling;

public enum Oppholdsårsak {
    INGEN, UTTAK_MØDREKVOTE_ANNEN_FORELDER, UTTAK_FEDREKVOTE_ANNEN_FORELDER, UTTAK_FORELDREPENGER_ANNEN_FORELDER,
    UTTAK_FELLESP_ANNEN_FORELDER(
            "periode.opphold.uttakfellesannen");

    public String key;

    Oppholdsårsak() {
        this(null);
    }

    Oppholdsårsak(String key) {
        this.key = key;
    }
}
