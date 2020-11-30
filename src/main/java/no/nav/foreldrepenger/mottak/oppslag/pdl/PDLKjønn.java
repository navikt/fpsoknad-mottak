package no.nav.foreldrepenger.mottak.oppslag.pdl;

record PDLKjønn(PDLKjønn.Kjønn kjønn) {

    static PDLKjønn mann() {
        return new PDLKjønn(Kjønn.MANN);
    }

    static PDLKjønn kvinne() {
        return new PDLKjønn(Kjønn.KVINNE);
    }

    static enum Kjønn {
        MANN,
        KVINNE,
        UKJENT
    }
}