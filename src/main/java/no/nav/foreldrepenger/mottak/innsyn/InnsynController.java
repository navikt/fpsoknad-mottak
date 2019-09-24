package no.nav.foreldrepenger.mottak.innsyn;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.Uttaksplan;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.security.token.support.core.api.ProtectedWithClaims;

@RestController
@RequestMapping(path = InnsynController.INNSYN, produces = APPLICATION_JSON_VALUE)
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class InnsynController {

    public static final String INNSYN = "/innsyn";

    private final Oppslag oppslag;
    private final Innsyn innsyn;

    public InnsynController(Innsyn innsyn, Oppslag oppslag) {
        this.innsyn = innsyn;
        this.oppslag = oppslag;
    }

    @GetMapping(value = "/saker")
    public List<Sak> saker() {
        return innsyn.hentSaker(oppslag.getAktørId());
    }

    @GetMapping(value = "/uttaksplan")
    public Uttaksplan uttaksplan(@RequestParam(name = "saksnummer") String saksnummer) {
        return innsyn.hentUttaksplan(saksnummer);
    }

    @GetMapping(value = "/uttaksplanannen")
    public Uttaksplan uttaksplan(
            @RequestParam(name = "annenPart") Fødselsnummer annenPart) {
        return innsyn.hentUttaksplan(oppslag.getAktørId(), oppslag.getAktørId(annenPart));
    }

    @GetMapping(value = "/vedtak")
    public Vedtak vedtak(@RequestParam(name = "saksnummer") String saksnummer) {
        return innsyn.hentVedtak(oppslag.getAktørId(), saksnummer);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [innsyn=" + innsyn + ", oppslag=" + oppslag + "]";
    }
}
