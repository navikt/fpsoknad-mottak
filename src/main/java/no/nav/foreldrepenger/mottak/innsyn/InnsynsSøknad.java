package no.nav.foreldrepenger.mottak.innsyn;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Data
public class InnsynsSøknad {

    private final Versjon versjon;
    private final Søknad søknad;
    private final String journalpostId;
}
