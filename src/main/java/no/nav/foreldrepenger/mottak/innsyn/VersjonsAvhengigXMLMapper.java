package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.mottak.innsyn.XMLMapper.VERSJONERBAR;
import static no.nav.foreldrepenger.mottak.util.Versjon.ALL;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.http.errorhandling.UnsupportedVersionException;
import no.nav.foreldrepenger.mottak.util.DefaultDokumentTypeAnalysator;
import no.nav.foreldrepenger.mottak.util.DokumentAnalysator;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
@Qualifier(VERSJONERBAR)
public class VersjonsAvhengigXMLMapper implements XMLMapper {

    private final List<XMLMapper> mappers;
    private final DokumentAnalysator analysator;

    public VersjonsAvhengigXMLMapper(XMLMapper... mappers) {
        this(new DefaultDokumentTypeAnalysator(), mappers);
    }

    public VersjonsAvhengigXMLMapper(DokumentAnalysator analysator, XMLMapper... mappers) {
        this(analysator, asList(mappers));
    }

    @Inject
    public VersjonsAvhengigXMLMapper(DokumentAnalysator analysator, List<XMLMapper> mappers) {
        this.mappers = mappers;
        this.analysator = analysator;
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
        Versjon versjon = analysator.versjon(xml);
        return mappers.stream()
                .filter(s -> s.versjon().equals(versjon))
                .findFirst()
                .orElseThrow(() -> new UnsupportedVersionException(versjon));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + ", analysator=" + analysator + "]";
    }
}
