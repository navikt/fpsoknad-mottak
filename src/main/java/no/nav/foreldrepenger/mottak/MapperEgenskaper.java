package no.nav.foreldrepenger.mottak;

import java.util.List;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskaper;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class MapperEgenskaper {
    private final Versjon versjon;
    private final List<SøknadType> typer;

    public MapperEgenskaper(Versjon versjon, List<SøknadType> typer) {
        this.versjon = versjon;
        this.typer = typer;
    }

    public Versjon getVersjon() {
        return versjon;
    }

    public List<SøknadType> getTyper() {
        return typer;
    }

    public boolean kanMappe(SøknadEgenskaper søknadEgenskaper) {
        return versjon.equals(søknadEgenskaper.getVersjon()) && typer.contains(søknadEgenskaper.getType());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [versjon=" + versjon + ", typer=" + typer + "]";
    }

}
