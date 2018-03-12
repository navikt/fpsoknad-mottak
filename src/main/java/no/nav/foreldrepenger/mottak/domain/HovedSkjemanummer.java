package no.nav.foreldrepenger.mottak.domain;

public enum HovedSkjemanummer {
    ENGANGSSTØNAD_FØDSEL("I000003", "Søknad engangsstønad fødsel", true);

    public final String id;
    public final String beskrivelse;
    public final boolean erPaakrevd;

    HovedSkjemanummer(String id, String beskrivelse, boolean erPaakrevd) {
        this.id = id;
        this.beskrivelse = beskrivelse;
        this.erPaakrevd = erPaakrevd;
    }
}
