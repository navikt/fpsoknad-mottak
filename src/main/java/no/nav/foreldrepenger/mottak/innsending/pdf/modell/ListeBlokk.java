package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
public class ListeBlokk extends Blokk {
    private final String tittel;
    private final List<String> punkter;

    @JsonCreator
    public ListeBlokk(String tittel, List<String> punkter) {
        this.tittel = tittel;
        this.punkter = punkter;
    }

    public ListeBlokk(String tittel, String... punkter) {
        this(tittel, List.of(punkter));
    }

    public String getTittel() {
        return tittel;
    }

    public List<String> getPunkter() {
        return punkter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ListeBlokk) o;
        return Objects.equals(tittel, that.tittel) && Objects.equals(punkter, that.punkter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tittel, punkter);
    }
}
