package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import lombok.Data;

@Data
public class SøknadWrapper {
    private final String xml;
    private final String journalpostId;
}
