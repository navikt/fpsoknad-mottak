package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.util.Pair;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class SøknadEgenskap {
    Pair<Versjon, SøknadType> egenskap;

    public static final SøknadEgenskap ETTERSENDING_FORELDREPENGER = new SøknadEgenskap(
            SøknadType.ETTERSENDING_FORELDREPENGER);
    public static final SøknadEgenskap INITIELL_FORELDREPENGER = new SøknadEgenskap(
            SøknadType.INITIELL_FORELDREPENGER);
    public static final SøknadEgenskap DOKMOT_ENGANGSSTØNAD = new SøknadEgenskap(V1, SøknadType.INITIELL_ENGANGSSTØNAD);
    public static final SøknadEgenskap ENDRING_FORELDREPENGER = new SøknadEgenskap(SøknadType.ENDRING_FORELDREPENGER);
    public static final SøknadEgenskap UKJENT = new SøknadEgenskap(Versjon.UKJENT, SøknadType.UKJENT);

    public SøknadEgenskap(SøknadType type) {
        this(DEFAULT_VERSJON, type);
    }

    @JsonCreator
    public SøknadEgenskap(@JsonProperty("versjon") Versjon versjon, @JsonProperty("type") SøknadType type) {
        this.egenskap = Pair.of(versjon, type);
    }

    public Versjon getVersjon() {
        return egenskap.getFirst();
    }

    public SøknadType getType() {
        return egenskap.getSecond();
    }

    public Pair<Versjon, SøknadType> getEgenskap() {
        return egenskap;
    }

    public boolean erUkjent() {
        return getType().equals(SøknadType.UKJENT) || getVersjon().equals(Versjon.UKJENT);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (egenskap == null ? 0 : egenskap.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SøknadEgenskap other = (SøknadEgenskap) obj;
        if (egenskap == null) {
            if (other.egenskap != null) {
                return false;
            }
        }
        else if (!egenskap.equals(other.egenskap)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [versjon=" + egenskap.getFirst() + ", søknadType=" + egenskap.getSecond()
                + "]";
    }
}
