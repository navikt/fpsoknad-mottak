package no.nav.foreldrepenger.mottak.innsending.pdf;

public enum PdfOutlineItems {
    SØKNAD("Søknad"),
    INFOSKRIV("Informasjon til arbeidsgiver(e)");

    private String title;

    PdfOutlineItems(String dekode) {
        this.title = dekode;
    }

    public String getTitle() {
        return title;
    }

}
