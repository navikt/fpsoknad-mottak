package no.nav.foreldrepenger.mottak.innsyn;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Behandling {
    private final String status;
    private final String type;
    private final String tema;
    private final String årsak;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
    private final InnsynsSøknad søknad;

}
