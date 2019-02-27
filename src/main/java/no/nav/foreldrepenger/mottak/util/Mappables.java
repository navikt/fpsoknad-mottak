package no.nav.foreldrepenger.mottak.util;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.errorhandling.UnsupportedEgenskapException;
import no.nav.foreldrepenger.mottak.innsending.mappers.Mappable;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public class Mappables {

    private Mappables() {

    }

    public static final String DELEGERENDE = "delegerende";

    private static final Logger LOG = LoggerFactory.getLogger(Mappables.class);

    public static <T extends Mappable> MapperEgenskaper egenskaperFor(List<T> mappables) {
        return new MapperEgenskaper(mappables.stream()
                .map(e -> e.mapperEgenskaper())
                .map(e -> e.getEgenskaper())
                .flatMap(e -> e.stream())
                .collect(toList()));
    }

    public static <T extends Mappable> T mapperFor(List<T> mappables, SøknadEgenskap egenskap) {
        T mapper = mappables.stream()
                .filter(m -> m.kanMappe(egenskap))
                .findFirst()
                .orElseThrow(() -> new UnsupportedEgenskapException(egenskap));
        LOG.info("Bruker mapper {} for {}", mapper.getClass().getSimpleName(), egenskap);
        return mapper;
    }

}
