package no.nav.foreldrepenger.mottak.innsyn;

import lombok.Data;

@Data
public class SøknadWrapper {
    private final String xml;
    private final String journalpostId;
}
