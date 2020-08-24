package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.List;

import no.nav.foreldrepenger.mottak.http.Pingable;

public interface Arbeidsforhold extends Pingable {

    List<EnkeltArbeidsforhold> hentAktiveArbeidsforhold();

}
