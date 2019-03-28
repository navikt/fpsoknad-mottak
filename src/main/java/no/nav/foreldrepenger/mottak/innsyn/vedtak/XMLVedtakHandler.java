package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import static no.nav.foreldrepenger.mottak.AbstractXMLInspektør.VEDTAK;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers.XMLVedtakMapper;

@Component
public class XMLVedtakHandler {

    private final XMLVedtakMapper mapper;
    private final Inspektør inspektør;

    public XMLVedtakHandler(@Qualifier(DELEGERENDE) XMLVedtakMapper mapper,
            @Qualifier(VEDTAK) Inspektør inspektør) {
        this.inspektør = inspektør;
        this.mapper = mapper;
    }

    public SøknadEgenskap inspiser(String xml) {
        return inspektør.inspiser(xml);
    }

    public Vedtak tilVedtak(String xml, SøknadEgenskap e) {
        return mapper.tilVedtak(xml, e);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + ", inspektør=" + inspektør + "]";
    }
}
