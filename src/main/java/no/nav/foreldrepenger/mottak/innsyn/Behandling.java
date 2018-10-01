package no.nav.foreldrepenger.mottak.innsyn;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.Søknad;

@Data
public class Behandling {
    private final String status;
    private final String type;
    private final String tema;
    private final String årsak;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
    private final Søknad søknad;

}
