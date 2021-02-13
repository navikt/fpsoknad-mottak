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
    public static final String FØRSTEGANGSSØKNAD = "Førstegangssøknad";
    public static final String ENDRINGSSØKNAD = "Endringssøknad";
    Pair<Versjon, SøknadType> egenskap;
    public static final SøknadEgenskap INITIELL_SVANGERSKAPSPENGER = of(SøknadType.INITIELL_SVANGERSKAPSPENGER);
    public static final SøknadEgenskap ETTERSENDING_SVANGERSKAPSPENGER = of(SøknadType.ETTERSENDING_SVANGERSKAPSPENGER);
    public static final SøknadEgenskap ETTERSENDING_FORELDREPENGER = of(SøknadType.ETTERSENDING_FORELDREPENGER);
    public static final SøknadEgenskap ETTERSENDING_ENGANGSSTØNAD = of(SøknadType.ETTERSENDING_ENGANGSSTØNAD);
    public static final SøknadEgenskap INITIELL_FORELDREPENGER = of(SøknadType.INITIELL_FORELDREPENGER);
    public static final SøknadEgenskap INITIELL_ENGANGSSTØNAD = of(SøknadType.INITIELL_ENGANGSSTØNAD);
    public static final SøknadEgenskap ENDRING_FORELDREPENGER = of(SøknadType.ENDRING_FORELDREPENGER);
    public static final SøknadEgenskap DOKMOT_ES_V1 = new SøknadEgenskap(V1, SøknadType.INITIELL_ENGANGSSTØNAD_DOKMOT);
    public static final SøknadEgenskap UKJENT = new SøknadEgenskap(Versjon.UKJENT, SøknadType.UKJENT);

    public static SøknadEgenskap of(SøknadType type) {
        return new SøknadEgenskap(type);
    }

    @Deprecated
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

    public boolean erEttersending() {
        return getType().erEttersending();
    }

    public boolean erEndring() {
        return getType().erEndring();
    }

    public boolean erInitiellForeldrepenger() {
        return getType().erInitiellForeldrepenger();
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
