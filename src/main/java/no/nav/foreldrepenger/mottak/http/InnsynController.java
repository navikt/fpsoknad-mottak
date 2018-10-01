package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsyn.Innsyn;
import no.nav.security.oidc.api.ProtectedWithClaims;

@RestController
@RequestMapping(path = InnsynController.INNSYN, produces = APPLICATION_JSON_VALUE)
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class InnsynController {

    private static final Logger LOG = LoggerFactory.getLogger(InnsynController.class);

    public static final String INNSYN = "/innsyn";

    private final Oppslag oppslag;
    private final Innsyn innsyn;

    public InnsynController(Innsyn innsyn, Oppslag oppslag) {
        this.innsyn = innsyn;
        this.oppslag = oppslag;
    }

    @GetMapping(value = "/soknad")
    public Søknad søknad(@RequestParam(name = "behandlingId") String behandlingId) {
        return innsyn.hentSøknad(behandlingId);
    }

    @GetMapping(value = "/saker")
    public List<Sak> saker() {
        return innsyn.hentSaker(oppslag.getAktørId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [innsyn=" + innsyn + ", oppslag=" + oppslag + "]";
    }
}
