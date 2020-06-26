package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.List;

import no.nav.foreldrepenger.mottak.http.RetryAware;
import no.nav.foreldrepenger.mottak.innsending.Pingable;

public interface Arbeidsforhold extends Pingable, RetryAware {

    List<EnkeltArbeidsforhold> hentAktiveArbeidsforhold();

}
