package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.List;

import no.nav.foreldrepenger.mottak.http.RetryAware;

public interface Arbeidsforhold extends /* Pingable, */ RetryAware {

    List<EnkeltArbeidsforhold> hentAktiveArbeidsforhold();

}
