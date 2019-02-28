package no.nav.foreldrepenger.mottak.innsyn.mappers;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public class UkjentXMLMapper implements XMLSøknadMapper {

    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(Versjon.UKJENT, SøknadType.UKJENT);

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return EGENSKAPER;
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        return null;
    }

}
