package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.util.StringUtil.limit;
import static no.nav.foreldrepenger.mottak.util.Versjon.UKJENT_VERSJON;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public class UkjentXMLVedtakMapper implements XMLVedtakMapper {

    private static final Logger LOG = LoggerFactory.getLogger(UkjentXMLVedtakMapper.class);

    @Override
    public List<Versjon> versjoner() {
        return UKJENT_VERSJON;
    }

    @Override
    public Vedtak tilVedtak(String xml, Versjon v) {
        LOG.info("Kan ikke mappe {}", limit(xml));
        return null;
    }
}
