package no.nav.foreldrepenger.mottak.innsending.pdf;

enum PdfOutlineItem {
    SØKNAD_OUTLINE("Søknad"),
    FORELDREPENGER_OUTLINE("Søknad om foreldrepenger"),
    INFOSKRIV_OUTLINE("Informasjon til arbeidsgiver(e)");

    private final String title;

    PdfOutlineItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
