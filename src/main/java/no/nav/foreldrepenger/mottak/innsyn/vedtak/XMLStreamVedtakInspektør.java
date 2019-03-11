package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.AbstractInspektør;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public final class XMLStreamVedtakInspektør extends AbstractInspektør implements XMLVedtakInspektør {

    @Override
    public Versjon inspiser(String xml) {
        return versjonFraXML(xml);
    }
}
