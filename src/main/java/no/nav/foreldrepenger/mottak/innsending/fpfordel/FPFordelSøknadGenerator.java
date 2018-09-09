package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;

@Component
public class FPFordelSøknadGenerator {

    private final XMLToDomainMapper xmlMapper;
    private final DomainToXMLMapper domainMapper;

    public FPFordelSøknadGenerator(XMLToDomainMapper xmlMapper, DomainToXMLMapper domainMapper) {
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
