package no.nav.foreldrepenger.mottak.oppslag;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.security.token.support.core.api.Unprotected;

@ProtectedRestController(OppslagController.OPPSLAG_PATH)
public class OppslagController {

    public static final Logger LOG = LoggerFactory.getLogger(OppslagController.class);

    public static final String OPPSLAG_PATH = "/oppslag";

    private final OppslagTjeneste oppslag;
    private final TokenUtil tokenUtil;

    public OppslagController(OppslagTjeneste oppslag, TokenUtil tokenUtil) {
        this.oppslag = oppslag;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping("/aktoer")
    public AktørId aktør() {
        LOG.info("Tjeneste kalles aktoer");
        return oppslag.aktørId(tokenUtil.autentisertBrukerOrElseThrowException());
    }

    @GetMapping("/ping")
    @Unprotected
    public String ping() {
        return oppslag.ping();
    }

    @GetMapping("/fnr")
    public Fødselsnummer fnr(@Valid @RequestParam(name = "aktorId") AktørId aktorId) {
        LOG.info("Tjeneste kalles fnr");
        return oppslag.fnr(aktorId);
    }

    @GetMapping("/navn")
    public Navn navn(@Valid @RequestParam(name = "aktorId") AktørId aktorId) {
        LOG.info("Tjeneste kalles navn");
        return oppslag.navn(aktorId.value());
    }

    @GetMapping("/navnfnr")
    public Navn navnfnr(@Valid @RequestParam(name = "fnr") Fødselsnummer fnr) {
        LOG.info("Tjeneste kalles navnfnr");
        return oppslag.navn(fnr.value());
    }

    @GetMapping("/person")
    public Person person() {
        return oppslag.person();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", tokenUtil=" + tokenUtil + "]";
    }

}
