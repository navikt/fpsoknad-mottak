package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import java.util.Objects;

public class FritekstBlokk extends Blokk {
    private final String tekst;

    public FritekstBlokk(String tekst) {
        this.tekst = tekst;
    }

    public String getTekst() {
        return tekst;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FritekstBlokk that = (FritekstBlokk) o;
        return Objects.equals(tekst, that.tekst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tekst);
    }
}
