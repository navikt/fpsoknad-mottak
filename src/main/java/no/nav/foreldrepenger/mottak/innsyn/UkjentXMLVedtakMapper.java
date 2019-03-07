package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.util.Versjon.UKJENT_VERSJON;

import java.util.List;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public class UkjentXMLVedtakMapper implements XMLVedtakMapper {

    @Override
    public List<Versjon> versjoner() {
        return UKJENT_VERSJON;
    }

    @Override
    public Vedtak tilVedtak(String xml, Versjon v) {
        return null;
    }
}
