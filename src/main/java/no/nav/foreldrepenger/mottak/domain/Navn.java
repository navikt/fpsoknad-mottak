package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.util.StringUtil.mask;

import java.util.Objects;

import com.google.common.base.Joiner;

import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;

public record Navn(String fornavn, String mellomnavn, String etternavn, Kjønn kjønn) {

    public String navn() {
        return Joiner.on(' ').skipNulls().join(fornavn(), mellomnavn(), etternavn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(etternavn, fornavn, mellomnavn);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Navn other = (Navn) obj;
        return Objects.equals(etternavn, other.etternavn) && Objects.equals(fornavn, other.fornavn)
                && Objects.equals(mellomnavn, other.mellomnavn);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fornavn=" + fornavn() + ", mellomnavn=" + mask(mellomnavn()) + ", etternavn="
                + mask(etternavn()) + ", kjønn=" + kjønn + "]";
    }
}
