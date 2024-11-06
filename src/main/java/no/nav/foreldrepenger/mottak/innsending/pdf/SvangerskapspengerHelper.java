package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

import java.util.List;
import java.util.Optional;

import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

public final class SvangerskapspengerHelper {

    private SvangerskapspengerHelper() {
    }

    public static Optional<String> virksomhetsnavn(List<EnkeltArbeidsforhold> arbeidsgivere, String arbeidsgiverId) {
        return safeStream(arbeidsgivere).filter(arb -> arb.arbeidsgiverId().equals(arbeidsgiverId))
            .findFirst()
            .map(EnkeltArbeidsforhold::arbeidsgiverNavn);
    }
}
