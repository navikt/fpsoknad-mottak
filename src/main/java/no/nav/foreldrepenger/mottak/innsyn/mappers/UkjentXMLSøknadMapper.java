package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper.UKJENT;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;

@Component
public class UkjentXMLSøknadMapper implements XMLSøknadMapper {
    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return UKJENT;
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        return null;
    }
}
