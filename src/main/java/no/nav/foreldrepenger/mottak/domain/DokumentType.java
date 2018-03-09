package no.nav.foreldrepenger.mottak.domain;

public enum DokumentType {

    ENGANGSSTØNAD_FØDSEL("I000003");

    public final String id;

    DokumentType(String type) {
        this.id = type;
    }

}
