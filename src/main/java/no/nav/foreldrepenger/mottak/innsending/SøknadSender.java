package no.nav.foreldrepenger.mottak.innsending;

import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;

public interface SøknadSender {

    Kvittering søk(Søknad søknad, SøknadEgenskap egenskap, InnsendingPersonInfo person);

    Kvittering ettersend(Ettersending ettersending, SøknadEgenskap egenskap, InnsendingPersonInfo person);

    Kvittering endreSøknad(Endringssøknad endringsøknad, SøknadEgenskap egenskap, InnsendingPersonInfo person);

}
