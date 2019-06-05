package no.nav.foreldrepenger.mottak.innsending.pdf;

public enum PdfOutlineItems {
    SØKNAD_OUTLINE("Søknad"),
    FORELDREPENGER_OUTLINE("Søknad om foreldrepenger"),
    INFOSKRIV_OUTLINE("Informasjon til arbeidsgiver(e)");

    private String title;

    PdfOutlineItems(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
