package no.nav.foreldrepenger.mottak.oppslag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@ProtectedRestController(OppslagController.OPPSLAG)
public class OppslagController {

    public static final String OPPSLAG = "/oppslag";

    private final OppslagTjeneste oppslag;
    private final TokenUtil tokenUtil;

    public OppslagController(OppslagTjeneste oppslag, TokenUtil tokenUtil) {
        this.oppslag = oppslag;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping("/aktoer")
    public AktørId aktør() {
        return oppslag.aktørId(tokenUtil.fnr());
    }

    @GetMapping("/fnr")
    public Fødselsnummer fnr(@RequestParam(name = "aktorId") AktørId aktorId) {
        return oppslag.fnr(aktorId);
    }

    @GetMapping("/navn")
    public Navn navn(@RequestParam(name = "aktorId") AktørId aktorId) {
        return oppslag.navn(aktorId.getId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", tokenUtil=" + tokenUtil + "]";
    }

}
