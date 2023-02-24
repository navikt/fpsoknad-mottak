package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import java.util.List;
import java.util.Objects;

public class TabellRad extends Blokk {
    private final String venstreTekst;
    private final String høyreTekst;
    private final List<? extends Blokk> underBlokker;

    public TabellRad(String venstreTekst, String høyreTekst, List<? extends Blokk> underBlokker) {
        this.venstreTekst = venstreTekst;
        this.høyreTekst = høyreTekst;
        this.underBlokker = underBlokker;
    }

    public String getVenstreTekst() {
        return venstreTekst;
    }

    public String getHøyreTekst() {
        return høyreTekst;
    }

    public List<? extends Blokk> getUnderBlokker() {
        return underBlokker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TabellRad tabellRad = (TabellRad) o;
        return Objects.equals(venstreTekst, tabellRad.venstreTekst) && Objects.equals(høyreTekst, tabellRad.høyreTekst) && Objects.equals(underBlokker, tabellRad.underBlokker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(venstreTekst, høyreTekst, underBlokker);
    }
}
