package no.nav.foreldrepenger.mottak.domain.felles;

public enum VedleggSkjemanummer {
    SØKNAD_FOELDREPEMGER("I000005", "Søknad om foreldrepenger"), TERMINBEKREFTELSE("I000062", "Terminbekreftelse");

    public final String skjemaNummer;
    public final String beskrivelse;

    VedleggSkjemanummer(String skjemaNummer) {
        this(skjemaNummer, null);
    }

    VedleggSkjemanummer(String skjemaNummer, String beskrivelse) {
        this.skjemaNummer = skjemaNummer;
        this.beskrivelse = beskrivelse;
    }
}
