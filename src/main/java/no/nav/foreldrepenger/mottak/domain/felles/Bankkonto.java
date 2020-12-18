package no.nav.foreldrepenger.mottak.domain.felles;

import static no.nav.foreldrepenger.mottak.util.StringUtil.mask;

public record Bankkonto(String kontonummer, String banknavn) {

    public static final Bankkonto UKJENT = new Bankkonto("", "");

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [kontonummer=" + mask(kontonummer) + ", banknavn=" + banknavn + "]";
    }
}
