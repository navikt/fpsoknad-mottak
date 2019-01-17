package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsyn.XMLMapper.VERSJONSBEVISST;
import static no.nav.foreldrepenger.mottak.util.Versjon.ALL;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.errorhandling.UnsupportedVersionException;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
@Qualifier(VERSJONSBEVISST)
public class DelegerendeXMLMapper implements XMLMapper {

    private final List<XMLMapper> mappers;
    private final SøknadInspektør inspektør;

    public DelegerendeXMLMapper(XMLMapper... mappers) {
        this(new XMLStreamSøknadInspektør(), mappers);
    }

    public DelegerendeXMLMapper(SøknadInspektør analysator, XMLMapper... mappers) {
        this(analysator, asList(mappers));
    }

    @Inject
    public DelegerendeXMLMapper(SøknadInspektør inspektør, List<XMLMapper> mappers) {
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

    @Override
    public List<SøknadType> typer() {
        return mappers.stream()
                .map(m -> m.typer())
                .flatMap(s -> s.stream())
                .collect(toList());
    }

    private XMLMapper mapper(String xml) {
        SøknadEgenskaper egenskaper = inspektør.inspiser(xml);
        return mappers.stream()
                .filter(mapper -> mapper.kanMappe(egenskaper))
                .findFirst()
                .orElseThrow(() -> new UnsupportedVersionException(egenskaper));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + ", inspektør=" + inspektør + "]";
    }
}
