package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.defaultVersjon;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.domain.FagsakType;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.util.Pair;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class SøknadEgenskap {
    Pair<Versjon, SøknadType> egenskap;

    public static final SøknadEgenskap INITIELL_SVANGERSKAPSPENGER = new SøknadEgenskap(
            SøknadType.INITIELL_SVANGERSKAPSPENGER);
    public static final SøknadEgenskap ETTERSENDING_SVANGERSKAPSPENGER = new SøknadEgenskap(
            SøknadType.ETTERSENDING_SVANGERSKAPSPENGER);
    public static final SøknadEgenskap ETTERSENDING_FORELDREPENGER = new SøknadEgenskap(
            SøknadType.ETTERSENDING_FORELDREPENGER);
    public static final SøknadEgenskap ETTERSENDING_ENGANGSSTØNAD = new SøknadEgenskap(
            SøknadType.ETTERSENDING_ENGANGSSTØNAD);
    public static final SøknadEgenskap INITIELL_FORELDREPENGER = new SøknadEgenskap(
            SøknadType.INITIELL_FORELDREPENGER);
    public static final SøknadEgenskap INITIELL_ENGANGSSTØNAD = new SøknadEgenskap(
            SøknadType.INITIELL_ENGANGSSTØNAD);
    public static final SøknadEgenskap ENDRING_FORELDREPENGER = new SøknadEgenskap(SøknadType.ENDRING_FORELDREPENGER);

    public static final SøknadEgenskap DOKMOT_ES_V1 = new SøknadEgenskap(V1, SøknadType.INITIELL_ENGANGSSTØNAD);
    public static final SøknadEgenskap UKJENT = new SøknadEgenskap(Versjon.UKJENT, SøknadType.UKJENT);

    public SøknadEgenskap(SøknadType type) {
        this(defaultVersjon(type), type);
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

    public FagsakType getFagsakType() {
        return getType().fagsakType();
    }

    public Pair<Versjon, SøknadType> getEgenskap() {
        return egenskap;
    }

    public boolean erUkjent() {
        return getType().erUkjent() || getVersjon().erUkjent();
    }

    @Override
    public int hashCode() {
        return Objects.hash(egenskap);
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
        return Objects.equals(getVersjon(), other.getVersjon()) && Objects.equals(getType(), other.getType());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [versjon=" + egenskap.getFirst() + ", søknadType=" + egenskap.getSecond()
                + "]";
    }
}
