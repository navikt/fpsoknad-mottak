package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.AbstractXMLInspektør.SØKNAD;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsyn.mappers.XMLSøknadMapper;

@Component
public class XMLSøknadHandler {

    private final XMLSøknadMapper mapper;
    private final XMLInspektør inspektør;

    public XMLSøknadHandler(@Qualifier(DELEGERENDE) XMLSøknadMapper mapper,
            @Qualifier(SØKNAD) XMLInspektør inspektør) {
        this.inspektør = inspektør;
        this.mapper = mapper;
    }

    public SøknadEgenskap inspiser(String xml) {
        return inspektør.inspiser(xml);
    }

    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        return mapper.tilSøknad(xml, egenskap);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + ", inspektør=" + inspektør + "]";
    }
}
