package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.AbstractInspektør.SØKNAD;
import static no.nav.foreldrepenger.mottak.innsending.mappers.Mappables.DELEGERENDE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.mappers.XMLSøknadMapper;

@Component
public class XMLSøknadHandler {

    private final XMLSøknadMapper mapper;
    private final Inspektør inspektør;
    private static final Logger LOG = LoggerFactory.getLogger(XMLSøknadHandler.class);

    public XMLSøknadHandler(@Qualifier(DELEGERENDE) XMLSøknadMapper mapper, @Qualifier(SØKNAD) Inspektør inspektør) {
        this.inspektør = inspektør;
        this.mapper = mapper;
    }

    public SøknadEgenskap inspiser(String xml) {
        return inspektør.inspiser(xml);
    }

    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        LOG.info("Konverterer til søknad for {}", egenskap);
        return mapper.tilSøknad(xml, egenskap);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + ", inspektør=" + inspektør + "]";
    }
}
