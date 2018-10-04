package no.nav.foreldrepenger.mottak.innsyn;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.Søknad;

@Data
public class InnsynsSøknad {

    private final Søknad søknad;
    private final String journalpostId;
}
