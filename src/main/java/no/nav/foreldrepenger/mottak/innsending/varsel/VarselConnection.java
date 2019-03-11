package no.nav.foreldrepenger.mottak.innsending.varsel;

import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;

public interface VarselConnection extends PingEndpointAware {

    void varsle(String xml);

}
