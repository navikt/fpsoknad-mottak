package no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto;

public record Konto(String kontonummer, UtenlandskKontoInfo utenlandskKontoInfo) {
    public static final Konto UKJENT = new Konto(null, null);
}
