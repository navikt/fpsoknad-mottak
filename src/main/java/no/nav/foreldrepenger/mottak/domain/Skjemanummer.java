package no.nav.foreldrepenger.mottak.domain;

public enum Skjemanummer {
    N6("I000047", "Terminbekreftelse", false);

    private final String dokumentTypeId;
    private final String beskrivelse;
    private final boolean erPaakrevd;

    Skjemanummer(String dokumentTypeId, String beskrivelse, boolean erPaakrevd) {
        this.dokumentTypeId = dokumentTypeId;
        this.beskrivelse = beskrivelse;
        this.erPaakrevd = erPaakrevd;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public String dokumentTypeId() {
        return dokumentTypeId;
    }

    public boolean erPaakrevd() {
        return erPaakrevd;
    }
}
