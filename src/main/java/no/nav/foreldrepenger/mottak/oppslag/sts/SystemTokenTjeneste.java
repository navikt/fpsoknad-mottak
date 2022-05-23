package no.nav.foreldrepenger.mottak.oppslag.sts;

import static no.nav.foreldrepenger.common.util.TokenUtil.BEARER;

import no.nav.foreldrepenger.mottak.http.Pingable;

public interface SystemTokenTjeneste extends Pingable {

    SystemToken getSystemToken();

    default String bearerToken() {
        return BEARER + getSystemToken().getToken();
    }

}
