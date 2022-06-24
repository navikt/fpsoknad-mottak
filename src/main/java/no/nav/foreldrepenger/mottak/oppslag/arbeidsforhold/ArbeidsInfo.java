package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.List;

import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.mottak.http.Pingable;

public interface ArbeidsInfo extends Pingable {

    List<EnkeltArbeidsforhold> hentArbeidsforhold();

    String orgnavn(Orgnummer orgnr);

}
