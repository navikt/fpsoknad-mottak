package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

public enum Dekningsgrad {

    GRAD80("80"), GRAD100("100");

    private final String kode;

    Dekningsgrad(String kode) {
        this.kode = kode;
    }

    public String kode() {
        return kode;
    }

}
