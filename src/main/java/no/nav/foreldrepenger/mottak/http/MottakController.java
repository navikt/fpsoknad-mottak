package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import javax.validation.Valid;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.innsending.dokmot.DokmotJMSSender;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelSøknadSender;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.api.Unprotected;

@RestController
@RequestMapping(path = MottakController.MOTTAK, produces = APPLICATION_JSON_VALUE)
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class MottakController {

    private static final Logger LOG = LoggerFactory.getLogger(MottakController.class);

    public static final String MOTTAK = "/mottak";

    private final FPFordelSøknadSender fpfordelSender;
    private final Oppslag oppslag;
    private final DokmotJMSSender dokmotSender;

    public MottakController(FPFordelSøknadSender sender, DokmotJMSSender dokmotSender, Oppslag oppslag) {
        this.fpfordelSender = sender;
        this.dokmotSender = dokmotSender;
        this.oppslag = oppslag;
    }

    @PostMapping(value = "/send")
    public ResponseEntity<Kvittering> send(@Valid @RequestBody Søknad søknad) {
        Person søker = oppslag.getSøker();
        MDC.put("Nav-Aktør-Id", søker.aktørId.getId());
        if (isForeldrepenger(søknad)) {
            return ok(fpfordelSender.send(søknad, søker));
        }
        return ok(dokmotSender.send(søknad, søker));
    }

    @PostMapping(value = "/ettersend")
    public ResponseEntity<Kvittering> send(@Valid @RequestBody Ettersending ettersending) {
        return ok(fpfordelSender.send(ettersending, oppslag.getSøker()));
    }

    @GetMapping(value = "/ping")
    @Unprotected
    public ResponseEntity<String> ping(@RequestParam(name = "navn", defaultValue = "earthling") String navn) {
        LOG.info("Jeg ble pinget");
        return ok("Hallo " + navn + " fra ubeskyttet ressurs");
    }

    @GetMapping(value = "/ping1")
    public ResponseEntity<String> ping1(@RequestParam(name = "navn", defaultValue = "earthling") String navn) {
        LOG.info("Jeg ble pinget");
        return ok("Hallo " + navn + " fra beskyttet ressurs");
    }

    private static boolean isForeldrepenger(Søknad søknad) {
        return søknad.getYtelse() instanceof Foreldrepenger;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sender=" + fpfordelSender + ", oppslagsService=" + oppslag
                + ", dokmotSender=" + dokmotSender + "]";
    }

}
