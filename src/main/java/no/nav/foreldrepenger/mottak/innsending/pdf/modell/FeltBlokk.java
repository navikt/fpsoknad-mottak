package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import java.util.Objects;


public class FeltBlokk extends Blokk {
    private String felt;
    private String verdi;

    public FeltBlokk(String felt, String verdi) {
        this.felt = felt;
        this.verdi = verdi;
    }

    public static FeltBlokk felt(String felt, String verdi) {
        return new FeltBlokk(felt, verdi);
    }

    public String getFelt() {
        return felt;
    }

    public String getVerdi() {
        return verdi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var feltBlokk = (FeltBlokk) o;
        return Objects.equals(felt, feltBlokk.felt) && Objects.equals(verdi, feltBlokk.verdi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felt, verdi);
    }
}
