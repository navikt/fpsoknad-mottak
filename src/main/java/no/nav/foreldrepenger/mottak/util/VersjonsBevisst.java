package no.nav.foreldrepenger.mottak.util;

import java.util.List;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;

public interface VersjonsBevisst {
    Versjon versjon();

    List<SøknadType> typer();

    default boolean kanMappe(SøknadEgenskaper egenskaper) {
        return versjon().equals(egenskaper.getVersjon()) && typer().contains(egenskaper.getType());
    }
}
