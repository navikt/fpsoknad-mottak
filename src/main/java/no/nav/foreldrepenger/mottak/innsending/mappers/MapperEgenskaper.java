package no.nav.foreldrepenger.mottak.innsending.mappers;

import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.common.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.util.Versjon.DEFAULT_SVP_VERSJON;
import static no.nav.foreldrepenger.common.util.Versjon.DEFAULT_VERSJON;
import static no.nav.foreldrepenger.common.util.Versjon.V1;
import static no.nav.foreldrepenger.common.util.Versjon.V2;
import static no.nav.foreldrepenger.common.util.Versjon.V3;

import java.util.Arrays;
import java.util.List;

import no.nav.foreldrepenger.common.innsending.SøknadType;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.util.Versjon;

public class MapperEgenskaper {
    private final List<SøknadEgenskap> egenskaper;
    public static final MapperEgenskaper SVANGERSKAPSPENGER = new MapperEgenskaper(DEFAULT_SVP_VERSJON,
            INITIELL_SVANGERSKAPSPENGER);
    public static final MapperEgenskaper UKJENT = new MapperEgenskaper(Versjon.UKJENT, SøknadType.UKJENT);
    public static final MapperEgenskaper FORELDREPENGER = new MapperEgenskaper(INITIELL_FORELDREPENGER,
            ENDRING_FORELDREPENGER);
    public static final MapperEgenskaper ALLE_FORELDREPENGER = new MapperEgenskaper(
            new SøknadEgenskap(V1, INITIELL_FORELDREPENGER),
            new SøknadEgenskap(V1, ENDRING_FORELDREPENGER),
            new SøknadEgenskap(V2, INITIELL_FORELDREPENGER),
            new SøknadEgenskap(V2, ENDRING_FORELDREPENGER),
            new SøknadEgenskap(V3, INITIELL_FORELDREPENGER),
            new SøknadEgenskap(V3, ENDRING_FORELDREPENGER));

    public MapperEgenskaper(Versjon versjon, SøknadType type) {
        this(new SøknadEgenskap(versjon, type));
    }

    public static MapperEgenskaper of(SøknadType... typer) {
        return new MapperEgenskaper(typer);
    }

    private MapperEgenskaper(SøknadType... typer) {
        this(DEFAULT_VERSJON, typer);
    }

    public MapperEgenskaper(Versjon versjon, SøknadType... typer) {
        this(typerForVersjon(versjon, typer));
    }

    MapperEgenskaper(SøknadEgenskap... egenskaper) {
        this(asList(egenskaper));
    }

    MapperEgenskaper(List<SøknadEgenskap> egenskaper) {
        this.egenskaper = egenskaper;
    }

    public List<SøknadEgenskap> getEgenskaper() {
        return egenskaper;
    }

    public boolean kanMappe(SøknadEgenskap egenskap) {
        return egenskaper.contains(egenskap);
    }

    private static List<SøknadEgenskap> typerForVersjon(final Versjon versjon, SøknadType... typer) {
        return Arrays.stream(typer)
                .map(type -> new SøknadEgenskap(versjon, type))
                .toList();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapperEgenskaper=" + egenskaper + "]";
    }
}
