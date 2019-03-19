package no.nav.foreldrepenger.mottak.util;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.errorhandling.UnsupportedEgenskapException;
import no.nav.foreldrepenger.mottak.innsending.mappers.Mappable;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public final class Mappables {

    private Mappables() {

    }

    public static final String DELEGERENDE = "delegerende";

    private static final Logger LOG = LoggerFactory.getLogger(Mappables.class);

    public static <T extends Mappable> MapperEgenskaper egenskaperFor(List<T> mappables) {
        return new MapperEgenskaper(mappables.stream()
                .map(Mappable::mapperEgenskaper)
                .map(MapperEgenskaper::getEgenskaper)
                .flatMap(Collection::stream)
                .collect(toList()));
    }

    public static <T extends Mappable> T mapperFor(List<T> mappables, SøknadEgenskap egenskap) {
        T mapper = mappables.stream()
                .filter(m -> m.kanMappe(egenskap))
                .findFirst()
                .orElseThrow(unsupported(mappables, egenskap));
        LOG.info("Bruker mapper {} for {}", mapper.getClass().getSimpleName(), egenskap);
        return mapper;
    }

    private static <T extends Mappable> Supplier<? extends UnsupportedEgenskapException> unsupported(List<T> mappables,
            SøknadEgenskap egenskap) {
        return () -> new UnsupportedEgenskapException(mappables, egenskap);
    }
}
