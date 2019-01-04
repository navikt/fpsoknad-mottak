package no.nav.foreldrepenger.mottak.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;

public class SøknadInspeksjonResultat {
    private final Pair<SøknadType, Versjon> resultat;

    @JsonCreator
    public SøknadInspeksjonResultat(@JsonProperty("type") SøknadType type, @JsonProperty("versjon") Versjon versjon) {
        this.resultat = Pair.of(type, versjon);
    }

    public Versjon versjon() {
        return resultat.getSecond();
    }

    public SøknadType type() {
        return resultat.getFirst();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [versjon=" + versjon() + ", søknadType=" + type() + "]";
    }
}
