package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.mottak.innsyn.XMLMapper.VERSJONSBEVISST;
import static no.nav.foreldrepenger.mottak.util.Versjon.ALL;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.http.errorhandling.UnsupportedVersionException;
import no.nav.foreldrepenger.mottak.util.DefaultSøknadInspektør;
import no.nav.foreldrepenger.mottak.util.SøknadInspektør;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
@Qualifier(VERSJONSBEVISST)
public class VersjonsBevisstXMLMapper implements XMLMapper {

    private final List<XMLMapper> mappers;
    private final SøknadInspektør inspektør;

    public VersjonsBevisstXMLMapper(XMLMapper... mappers) {
        this(new DefaultSøknadInspektør(), mappers);
    }

    public VersjonsBevisstXMLMapper(SøknadInspektør analysator, XMLMapper... mappers) {
        this(analysator, asList(mappers));
    }

    @Inject
    public VersjonsBevisstXMLMapper(SøknadInspektør inspektør, List<XMLMapper> mappers) {
        this.mappers = mappers;
        this.inspektør = inspektør;
    }

    @Override
    public Søknad tilSøknad(String xml) {
        return mapper(xml).tilSøknad(xml);
    }

    @Override
    public Versjon versjon() {
        return ALL;
    }

    private XMLMapper mapper(String xml) {
        Versjon versjon = inspektør.versjon(xml);
        return mappers.stream()
                .filter(s -> s.versjon().equals(versjon))
                .findFirst()
                .orElseThrow(() -> new UnsupportedVersionException(versjon));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + ", inspektør=" + inspektør + "]";
    }
}
