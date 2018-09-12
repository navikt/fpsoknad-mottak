package no.nav.foreldrepenger.mottak.http;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.EndringsSøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoSakStatus;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.SøknadsTjeneste;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.api.Unprotected;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = SøknadController.MOTTAK, produces = APPLICATION_JSON_VALUE)
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class SøknadController {

    private static final Logger LOG = LoggerFactory.getLogger(SøknadController.class);

    public static final String MOTTAK = "/mottak";

    private final SøknadsTjeneste søknadsTjeneste;
    private final Oppslag oppslag;
    private final SøknadSender sender;

    public SøknadController(@Qualifier("dual") SøknadSender sender, Oppslag oppslag, SøknadsTjeneste søknadsTjeneste) {
        this.sender = sender;
        this.oppslag = oppslag;
        this.søknadsTjeneste = søknadsTjeneste;
    }

    @PostMapping(value = "/send")
    public Kvittering send(@Valid @RequestBody Søknad søknad) {
        Person søker = oppslag.getSøker();
        MDC.put("Nav-Aktør-Id", søker.aktørId.getId());
        return sender.send(søknad, søker);
    }

    @PostMapping(value = "/ettersend")
    public Kvittering send(@Valid @RequestBody Ettersending ettersending) {
        return sender.send(ettersending, oppslag.getSøker());
    }

    @PostMapping(value = "/endre")
    public Kvittering send(@Valid @RequestBody EndringsSøknad endringsSøknad) {
        return sender.send(endringsSøknad, oppslag.getSøker());
    }

    @GetMapping(value = "/soknad")
    public Søknad søknad(@RequestParam(name = "behandlingId") String behandlingId) {
        return søknadsTjeneste.hentSøknad(behandlingId);
    }

    @GetMapping(value = "/ping")
    @Unprotected
    public String ping(@RequestParam(name = "navn", defaultValue = "earthling") String navn) {
        LOG.info("Jeg ble pinget");
        return "Hallo " + navn + " fra ubeskyttet ressurs";
    }

    @GetMapping(value = "/saker")
    public List<FPInfoSakStatus> saker() {
        return søknadsTjeneste.hentSaker(oppslag.getAktørId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [sender=" + sender + ", søknadsTjeneste=" + søknadsTjeneste
                + ", oppslag=" + oppslag + "]";
    }

}
