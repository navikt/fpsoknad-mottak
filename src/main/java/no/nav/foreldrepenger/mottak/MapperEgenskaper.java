package no.nav.foreldrepenger.mottak;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class MapperEgenskaper {
    private final List<SøknadEgenskap> søknadEgenskaper;

    public MapperEgenskaper(Versjon versjon, SøknadType type) {
        this(new SøknadEgenskap(versjon, type));
    }

    public MapperEgenskaper(Versjon versjon, SøknadType... typer) {
        this(typerForVersjon(versjon, typer));
    }

    public MapperEgenskaper(SøknadEgenskap egenskap) {
        this(singletonList(egenskap));
    }

    public MapperEgenskaper(List<SøknadEgenskap> søknadEgenskaper) {
        this.søknadEgenskaper = søknadEgenskaper;
    }

    public List<SøknadEgenskap> getSøknadEgenskaper() {
        return søknadEgenskaper;
    }

    public boolean kanMappe(SøknadEgenskap egenskap) {
        return søknadEgenskaper.contains(egenskap);
    }

    public boolean kanMappe(Versjon versjon) {
        return søknadEgenskaper.stream()
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
        return getClass().getSimpleName() + " [søknadEgenskaper=" + søknadEgenskaper + "]";
    }

}
