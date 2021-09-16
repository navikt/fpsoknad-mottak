package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import no.nav.foreldrepenger.common.innsending.mappers.Mappable;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsyn.vedtak.Vedtak;

public interface XMLVedtakMapper extends Mappable {

    Vedtak tilVedtak(String xml, SøknadEgenskap egenskap);

}
