package no.nav.foreldrepenger.mottak.innsyn;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class SøknadEgenskaper {
    private final SøknadType type;
    private final Versjon versjon;

    @JsonCreator
    public SøknadEgenskaper(@JsonProperty("type") SøknadType type, @JsonProperty("versjon") Versjon versjon) {
        this.type = type;
        this.versjon = versjon;
    }

    public Versjon getVersjon() {
        return versjon;
    }

    public SøknadType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, versjon);
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
        SøknadEgenskaper other = (SøknadEgenskaper) obj;
        if (type != other.type) {
            return false;
        }
        if (versjon != other.versjon) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [versjon=" + versjon + ", søknadType=" + type + "]";
    }
}
