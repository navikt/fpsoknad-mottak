package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.innsending.mappers.Mappable;

public interface XMLVedtakMapper extends Mappable {

    Vedtak tilVedtak(String xml, SøknadEgenskap egenskap);

}
