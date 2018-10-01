package no.nav.foreldrepenger.mottak.innsyn;

import lombok.Data;

@Data
public class Behandling {
    private String id;
    private final String status;
    private final String type;
    private final String tema;
    private final String årsak;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;

    public Behandling withId(String id) {
        setId(id);
        return this;
    }
}
