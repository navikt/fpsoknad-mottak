package no.nav.foreldrepenger.mottak.domain;

public enum Skjemanummer {
    N6("I000047", false);

    private final String dokumentTypeId;
    private final boolean erPaakrevd;

    Skjemanummer(String dokumentTypeId, boolean erPaakrevd) {
        this.dokumentTypeId = dokumentTypeId;
        this.erPaakrevd = erPaakrevd;
    }

    public String dokumentTypeId() {
        return dokumentTypeId;
    }

    public boolean erPaakrevd() {
        return erPaakrevd;
    }
}
