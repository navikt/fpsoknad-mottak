package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PDLAdresseBeskyttelse {
    private final PDLAdresseGradering gradering;

    @JsonCreator
    public PDLAdresseBeskyttelse(@JsonProperty("gradering") PDLAdresseGradering gradering) {
        this.gradering = gradering;
    }

    public PDLAdresseGradering getGradering() {
        return gradering;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [gradering=" + gradering + "]";
    }
}
