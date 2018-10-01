package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.http.Oppslag;

@Component
public class ForeldrepengerSøknadMapper {

    private final XMLTilSøknadMapper xmlMapper;
    private final SøknadTilXMLMapper domainMapper;

    @Inject
    public ForeldrepengerSøknadMapper(Oppslag oppslag) {
        this(new XMLTilSøknadMapper(oppslag), new SøknadTilXMLMapper(oppslag));
    }

    private ForeldrepengerSøknadMapper(XMLTilSøknadMapper xmlMapper, SøknadTilXMLMapper domainMapper) {
        this.xmlMapper = xmlMapper;
        this.domainMapper = domainMapper;
    }

    public Søknad tilSøknad(String søknadXml) {
        return xmlMapper.tilSøknad(søknadXml);
    }

    public String tilXML(Søknad søknad, AktorId søker) {
        return domainMapper.tilXML(søknad, søker);
    }

    public String tilXML(Endringssøknad endringssøknad, AktorId søker) {
        return domainMapper.tilXML(endringssøknad, søker);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [xmlMapper=" + xmlMapper + ", domainMapper=" + domainMapper + "]";
    }

}
