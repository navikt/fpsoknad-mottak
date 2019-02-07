package no.nav.foreldrepenger.mottak;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;

import java.util.Arrays;
import java.util.List;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class MapperEgenskaper {
    private final List<SøknadEgenskap> mapperEgenskaper;

    public MapperEgenskaper(Versjon versjon, SøknadType type) {
        this(new SøknadEgenskap(versjon, type));
    }

    public MapperEgenskaper(SøknadType... typer) {
        this(DEFAULT_VERSJON, typer);
    }

    public MapperEgenskaper(Versjon versjon, SøknadType... typer) {
        this(typerForVersjon(versjon, typer));
    }

    public MapperEgenskaper(SøknadEgenskap egenskap) {
        this(singletonList(egenskap));
    }

    public MapperEgenskaper(List<SøknadEgenskap> mapperEgenskaper) {
        this.mapperEgenskaper = mapperEgenskaper;
    }

    public List<SøknadEgenskap> getSøknadEgenskaper() {
        return mapperEgenskaper;
    }

    public boolean kanMappe(SøknadEgenskap egenskap) {
        return mapperEgenskaper.contains(egenskap);
    }

    public boolean kanMappe(Versjon versjon) {
        return mapperEgenskaper.stream()
                .map(e -> e.getVersjon())
                .filter(v -> v.equals(versjon))
                .findFirst()
                .isPresent();
    }

    private static List<SøknadEgenskap> typerForVersjon(final Versjon versjon, SøknadType[] typer) {
        return Arrays.stream(typer)
                .map(t -> new SøknadEgenskap(versjon, t))
                .collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapperEgenskaper=" + mapperEgenskaper + "]";
    }

}
