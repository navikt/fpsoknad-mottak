package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.AbstractInspektør.SØKNAD;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsyn.mappers.XMLSøknadMapper;

@Component
public class XMLSøknadHandler {

    private final XMLSøknadMapper mapper;
    private final Inspektør inspektør;

    public XMLSøknadHandler(@Qualifier(DELEGERENDE) XMLSøknadMapper mapper, @Qualifier(SØKNAD) Inspektør inspektør) {
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
