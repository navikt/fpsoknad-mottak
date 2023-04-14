package no.nav.foreldrepenger.mottak.oppslag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;
import no.nav.security.token.support.core.api.Unprotected;

@ProtectedRestController(OppslagController.OPPSLAG_PATH)
public class OppslagController {

    public static final Logger LOG = LoggerFactory.getLogger(OppslagController.class);

    public static final String OPPSLAG_PATH = "/oppslag";

    private final PDLConnection pdl;
    private final TokenUtil tokenUtil;

    public OppslagController(PDLConnection pdl, TokenUtil tokenUtil) {
        this.pdl = pdl;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping("/ping")
    @Unprotected
    public String ping() {
        return pdl.ping();
    }

    @GetMapping("/aktoer")
    public AktørId aktør() {
        return pdl.aktørId(fnr());
    }

    @GetMapping("/person")
    public Person person() {
        return pdl.hentPerson(fnr());
    }

    private Fødselsnummer fnr() {
        return tokenUtil.autentisertBrukerOrElseThrowException();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdl=" + pdl + ", tokenUtil=" + tokenUtil + "]";
    }

}
