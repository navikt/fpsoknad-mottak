package no.nav.foreldrepenger.mottak.domain;

public enum Skjemanummer {
    N6("I000047", "Terminbekreftelse", false);

    private final String dokumentTypeId;
    private final String beskrivelse;
    private final boolean erPaakrevd;

    Skjemanummer(String beskrivelse, String dokumentTypeId, boolean erPaakrevd) {
        this.beskrivelse = beskrivelse;
        this.dokumentTypeId = dokumentTypeId;
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
