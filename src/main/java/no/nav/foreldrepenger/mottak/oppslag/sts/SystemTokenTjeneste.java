package no.nav.foreldrepenger.mottak.oppslag.sts;

import no.nav.foreldrepenger.mottak.http.RetryAware;
import no.nav.foreldrepenger.mottak.innsending.Pingable;

public interface SystemTokenTjeneste extends RetryAware, Pingable {

    SystemToken getSystemToken();

}
