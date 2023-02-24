package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.Set;

import static no.nav.foreldrepenger.common.util.StringUtil.mask;

record PDLWrappedNavn(Set<PDLNavn> navn) {
}

record PDLNavn(String fornavn, String mellomnavn, String etternavn) {
    @Override
    public String toString() {
        return "PDLNavn{" +
            "fornavn='" + fornavn + '\'' +
            ", mellomnavn='" + mellomnavn + '\'' +
            ", etternavn='" + mask(etternavn) + '\'' +
            '}';
    }
}
