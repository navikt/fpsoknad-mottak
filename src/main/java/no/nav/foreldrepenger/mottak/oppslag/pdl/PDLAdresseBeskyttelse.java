package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class PDLAdresseBeskyttelse {
    private final PDLAdresseGradering gradering;

    @JsonCreator
    PDLAdresseBeskyttelse(@JsonProperty("gradering") PDLAdresseGradering gradering) {
        this.gradering = gradering;
    }

    PDLAdresseGradering getGradering() {
        return gradering;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [gradering=" + gradering + "]";
    }
}
