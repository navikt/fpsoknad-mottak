package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.Uttak;

@Data
public class Vedtak {
    private VedtakMetadata metadata;
    private final Uttak uttak;
    private final SaksInformasjon saksInformasjon;

    public Vedtak withMetadata(VedtakMetadata vedtakMetadata) {
        setMetadata(vedtakMetadata);
        return this;
    }

    @JsonIgnore
    public String getFagsakId() {
        return saksInformasjon.getFagsakId();
    }
}
