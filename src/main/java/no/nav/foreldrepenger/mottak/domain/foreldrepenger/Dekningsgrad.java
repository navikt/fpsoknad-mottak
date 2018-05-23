package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

public enum Dekningsgrad {

    GRAD80("80%"), GRAD100("100%");

    private final String txt;

    Dekningsgrad(String txt) {
        this.txt = txt;
    }

    public String txt() {
        return txt;
    }

}
