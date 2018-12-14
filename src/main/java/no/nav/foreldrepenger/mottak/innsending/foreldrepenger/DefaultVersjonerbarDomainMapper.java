package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static java.util.Arrays.asList;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.http.errorhandling.UnsupportedVersionException;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public class DefaultVersjonerbarDomainMapper implements VersjonerbarDomainMapper {

    private final List<DomainMapper> mappers;

    public DefaultVersjonerbarDomainMapper(DomainMapper... mappers) {
        this(asList(mappers));
    }

    @Inject
    public DefaultVersjonerbarDomainMapper(List<DomainMapper> mappers) {
        this.mappers = mappers;
    }

    @Override
    public Versjon versjon() {
        return Versjon.ALL;
    }

    @Override
    public String tilXML(Søknad søknad, AktorId søker) {
        return tilXML(søknad, søker, versjon());
    }

    @Override
    public String tilXML(Endringssøknad endringssøknad, AktorId søker) {
        return tilXML(endringssøknad, søker, versjon());
    }

    @Override
    public String tilXML(Søknad søknad, AktorId søker, Versjon versjon) {
        return mapper(versjon).tilXML(søknad, søker);
    }

    @Override
    public String tilXML(Endringssøknad endringssøknad, AktorId søker, Versjon versjon) {
        return mapper(versjon).tilXML(endringssøknad, søker);

    }

    private DomainMapper mapper(Versjon versjon) {
        return mappers.stream()
                .filter(s -> s.versjon().equals(versjon))
                .findFirst()
                .orElseThrow(() -> new UnsupportedVersionException(versjon));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + "]";
    }

}
