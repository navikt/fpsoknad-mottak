package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.util.VersjonsBevisst;

public interface XMLMapper extends VersjonsBevisst {

    String VERSJONSBEVISST = "multiple";

    Søknad tilSøknad(String xml);

}
