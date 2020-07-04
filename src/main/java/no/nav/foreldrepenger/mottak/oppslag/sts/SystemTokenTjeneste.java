package no.nav.foreldrepenger.mottak.oppslag.sts;

import no.nav.foreldrepenger.mottak.http.Pingable;
import no.nav.foreldrepenger.mottak.http.RetryAware;

public interface SystemTokenTjeneste extends RetryAware, Pingable {

    SystemToken getSystemToken();

}
