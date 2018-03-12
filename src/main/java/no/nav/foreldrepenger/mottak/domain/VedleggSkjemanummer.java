package no.nav.foreldrepenger.mottak.domain;

public enum VedleggSkjemanummer {
    TERMINBEKREFTELSE("I000062", "Terminbekreftelse");

    public final String id;
    public final String beskrivelse;

    VedleggSkjemanummer(String id, String beskrivelse) {
        this.id = id;
        this.beskrivelse = beskrivelse;
    }
}
