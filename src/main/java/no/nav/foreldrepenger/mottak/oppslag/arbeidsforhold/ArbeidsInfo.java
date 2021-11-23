package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.List;

import no.nav.foreldrepenger.mottak.http.Pingable;

public interface ArbeidsInfo extends Pingable {

    List<EnkeltArbeidsforhold> hentArbeidsforhold();

    String orgnavn(String orgnr);

}
