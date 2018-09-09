package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.http.Oppslag;

@Component
public class FPFordelSøknadMapper {

    private final XMLToDomainMapper xmlMapper;
    private final DomainToXMLMapper domainMapper;

    @Inject
    public FPFordelSøknadMapper(Oppslag oppslag) {
        this(new XMLToDomainMapper(oppslag), new DomainToXMLMapper(oppslag));
    }

    private FPFordelSøknadMapper(XMLToDomainMapper xmlMapper, DomainToXMLMapper domainMapper) {
        this.xmlMapper = xmlMapper;
        this.domainMapper = domainMapper;
    }

    public Søknad tilSøknad(String søknadXml) {
        return xmlMapper.tilSøknad(søknadXml);
    }

    public String tilXML(Søknad søknad, AktorId søker) {
        return domainMapper.tilXML(søknad, søker);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [xmlMapper=" + xmlMapper + ", domainMapper=" + domainMapper + "]";
    }

}
