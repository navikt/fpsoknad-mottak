package no.nav.foreldrepenger.mottak.domain.felles;

public enum VedleggSkjemanummer {
    SØKNAD_FOELDREPEMGER("I000005", "Søknad om foreldrepenger"), TERMINBEKREFTELSE("I000062", "Terminbekreftelse");

    public final String id;
    public final String beskrivelse;

    VedleggSkjemanummer(String id, String beskrivelse) {
        this.id = id;
        this.beskrivelse = beskrivelse;
    }
}
