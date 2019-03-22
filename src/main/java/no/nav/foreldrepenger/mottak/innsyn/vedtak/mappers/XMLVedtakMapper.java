package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import no.nav.foreldrepenger.mottak.innsending.mappers.Mappable;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;

public interface XMLVedtakMapper extends Mappable {

    static final String UKJENT_KODEVERKSVERDI = "-";

    Vedtak tilVedtak(String xml, SøknadEgenskap egenskap);

}
