package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.mappers.Mappable;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;

public interface MappablePdfGenerator extends Mappable {

    byte[] generer(Søknad søknad, SøknadEgenskap egenskap, InnsendingPersonInfo person);

}
