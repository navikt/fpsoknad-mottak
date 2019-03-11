package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import java.util.List;

import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.util.Versjon;

public interface XMLVedtakMapper {

    List<Versjon> versjoner();

    default boolean kanMappe(Versjon v) {
        return versjoner().contains(v);
    }

    Vedtak tilVedtak(String xml, Versjon v);

}
