package no.nav.foreldrepenger.mottak.innsending.varsel;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.PingEndpointAware;

public interface VarselConnection extends PingEndpointAware {

    void send(String xml);

    boolean isEnabled();

}
