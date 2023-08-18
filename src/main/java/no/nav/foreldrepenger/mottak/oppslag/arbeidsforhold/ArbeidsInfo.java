package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.List;

import no.nav.foreldrepenger.common.domain.Orgnummer;

public interface ArbeidsInfo {

    List<EnkeltArbeidsforhold> hentArbeidsforhold();

    String orgnavn(Orgnummer orgnr);

}
