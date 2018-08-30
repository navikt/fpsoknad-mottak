package no.nav.foreldrepenger.lookup.rest.fpinfo;

import lombok.Data;

@Data
public class BehandlingsLenke {
    private final String href;
    private final String rel;
    private final String type;
}
