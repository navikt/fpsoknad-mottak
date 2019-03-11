package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers.XMLVedtakMapper;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public class XMLVedtakHandler {

    private final XMLVedtakMapper mapper;
    private final XMLVedtakInspektør inspektør;

    public XMLVedtakHandler(@Qualifier(DELEGERENDE) XMLVedtakMapper mapper,
            XMLVedtakInspektør inspektør) {
        this.inspektør = inspektør;
        this.mapper = mapper;
    }

    public Versjon inspiser(String xml) {
        return inspektør.inspiser(xml);
    }

    public Vedtak tilVedtak(String xml, Versjon v) {
        return mapper.tilVedtak(xml, v);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + ", inspektør=" + inspektør + "]";
    }
}
