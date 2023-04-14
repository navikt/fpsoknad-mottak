package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import java.util.List;
import java.util.Objects;

public class TemaBlokk extends Blokk {
    private final String overskrift;
    private final List<? extends Blokk> underBlokker;

    public TemaBlokk(String overskrift, List<? extends Blokk> underBlokker) {
        this.overskrift = overskrift;
        this.underBlokker = underBlokker;
    }

    public String getOverskrift() {
        return overskrift;
    }

    public List<? extends Blokk> getUnderBlokker() {
        return underBlokker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var temaBlokk = (TemaBlokk) o;
        return Objects.equals(overskrift, temaBlokk.overskrift) && Objects.equals(underBlokker, temaBlokk.underBlokker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(overskrift, underBlokker);
    }
}
