package no.nav.foreldrepenger.mottak.innsending.pdf;

public enum PdfOutlineItem {
    SØKNAD_OUTLINE("Søknad"),
    FORELDREPENGER_OUTLINE("Søknad om foreldrepenger");

    private final String title;

    PdfOutlineItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
