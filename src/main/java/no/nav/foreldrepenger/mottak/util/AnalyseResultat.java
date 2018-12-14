package no.nav.foreldrepenger.mottak.util;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;

public class AnalyseResultat {
    private final Pair<SøknadType, Versjon> resultat;

    public AnalyseResultat(SøknadType type, Versjon versjon) {
        this.resultat = Pair.of(type, versjon);
    }

    public Versjon getVersjon() {
        return resultat.getSecond();
    }

    public SøknadType type() {
        return resultat.getFirst();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [versjon=" + getVersjon() + ", søknadType=" + type() + "]";
    }
}
