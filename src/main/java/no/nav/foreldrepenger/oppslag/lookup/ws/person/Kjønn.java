package no.nav.foreldrepenger.oppslag.lookup.ws.person;

public enum Kjønn {
    M("M"), K("K");

    private final String verdi;

    Kjønn(String verdi) {
        this.verdi = verdi;
    }

    public String getVerdi() {
        return verdi;
    }
}
