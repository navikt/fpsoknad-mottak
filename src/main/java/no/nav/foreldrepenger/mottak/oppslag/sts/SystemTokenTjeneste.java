package no.nav.foreldrepenger.mottak.oppslag.sts;

import no.nav.foreldrepenger.mottak.http.Pingable;

public interface SystemTokenTjeneste extends Pingable {

    SystemToken getSystemToken();

}
