package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import no.nav.foreldrepenger.mottak.domain.FagsakType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public record VedtakMetadata(String journalpostId, SøknadEgenskap e) {
    public FagsakType getType() {
        return e.getFagsakType();
    }

    public String getVersjon() {
        return e.getVersjon().name();
    }
}
