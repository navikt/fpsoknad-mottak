package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import static no.nav.foreldrepenger.mottak.util.StringUtil.limit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;

@Component
public class UkjentXMLVedtakMapper implements XMLVedtakMapper {

    private static final Logger LOG = LoggerFactory.getLogger(UkjentXMLVedtakMapper.class);

    @Override
    public Vedtak tilVedtak(String xml, SøknadEgenskap egenskap) {
        LOG.info("Kan ikke mappe {}", limit(xml));
        return null;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return MapperEgenskaper.UKJENT;
    }
}
