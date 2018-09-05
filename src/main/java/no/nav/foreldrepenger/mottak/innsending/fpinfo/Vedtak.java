package no.nav.foreldrepenger.mottak.innsending.fpinfo;

import lombok.Data;

@Data
public class Vedtak {
    private final String xmlClob;
    private final String journalpostId;
}
