package no.nav.foreldrepenger.mottak.innsyn;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.util.Versjon;

@Qualifier(DELEGERENDE)
@Component
public class DelegerendeXMLVedtakMapper implements XMLVedtakMapper {

    private final List<XMLVedtakMapper> mappers;
    private final List<Versjon> versjoner;

    public DelegerendeXMLVedtakMapper(List<XMLVedtakMapper> mappers) {
        this.mappers = mappers;
        this.versjoner = versjonerFor(mappers);
    }

    @Override
    public List<Versjon> versjoner() {
        return versjoner;
    }

    @Override
    public Vedtak tilVedtak(String xml, Versjon v) {
        return mapperFor(v).tilVedtak(xml, v);
    }

    private XMLVedtakMapper mapperFor(Versjon versjon) {
        return mappers.stream()
                .filter(m -> m.kanMappe(versjon))
                .findFirst()
                .orElse(new UkjentXMLVedtakMapper());
    }

    private static List<Versjon> versjonerFor(List<XMLVedtakMapper> mappers) {
        return mappers.stream()
                .map(e -> e.versjoner())
                .flatMap(e -> e.stream())
                .collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + ", versjoner=" + versjoner + "]";
    }
}
