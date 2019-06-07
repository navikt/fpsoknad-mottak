package no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling;

public enum MorsAktivitet {
    ARBEID, UTDANNING, KVALPROG, INTROPROG, TRENGER_HJELP("morsaktivitet.sykdom"), INNLAGT, ARBEID_OG_UTDANNING,
    SAMTIDIGUTTAK, UFØRE;

    private final String key;

    public String getKey() {
        return key;
    }

    MorsAktivitet() {
        this(null);
    }

    MorsAktivitet(String key) {
        this.key = key;
    }
}
