package no.nav.foreldrepenger.mottak.innsyn;

import lombok.Data;

@Data
public class Lenke {
    private final String href;
    private final String rel;
    private final String type;
}
