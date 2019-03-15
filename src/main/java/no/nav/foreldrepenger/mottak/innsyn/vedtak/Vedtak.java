package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import lombok.Data;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.Uttak;

@Data
public class Vedtak {
    private VedtakMetadata metadata;
    private final Uttak uttak;

    public Vedtak withMetadata(VedtakMetadata vedtakMetadata) {
        setMetadata(vedtakMetadata);
        return this;
    }
}
