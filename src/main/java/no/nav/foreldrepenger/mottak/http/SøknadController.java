package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import java.util.List;

import javax.inject.Inject;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.EndringsSøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.innsending.dokmot.DokmotJMSSender;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelSøknadSender;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoSakStatus;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.SøknadsTjeneste;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.api.Unprotected;

@RestController
@RequestMapping(path = SøknadController.MOTTAK, produces = APPLICATION_JSON_VALUE)
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class SøknadController {

    private static final Logger LOG = LoggerFactory.getLogger(SøknadController.class);

    public static final String MOTTAK = "/mottak";

    private final FPFordelSøknadSender fpfordelSender;
    private final SøknadsTjeneste søknadsTjeneste;
    private final Oppslag oppslag;
    private final DokmotJMSSender dokmotSender;
    @Inject
    SøknadsTjeneste saksClient;
    @Inject
    ObjectMapper mapper;

    public SøknadController(FPFordelSøknadSender sender, DokmotJMSSender dokmotSender, Oppslag oppslag,
            SøknadsTjeneste søknadsTjeneste, SøknadsTjeneste saksClient) {
        this.fpfordelSender = sender;
        this.dokmotSender = dokmotSender;
        this.oppslag = oppslag;
        this.søknadsTjeneste = søknadsTjeneste;
        this.saksClient = saksClient;
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

    @PostMapping(value = "/endre")
    public ResponseEntity<Kvittering> send(@Valid @RequestBody EndringsSøknad endringsSøknad) {
        return ok(fpfordelSender.send(endringsSøknad, oppslag.getSøker()));
    }

    @GetMapping(value = "/soknad")
    @Unprotected
    public Søknad søknad(@RequestParam(name = "behandlingId") String behandlingId) throws JsonProcessingException {
        Søknad søknad = søknadsTjeneste.hentSøknad(behandlingId);
        LOG.info("Konvertert søknad er {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(søknad));
        return søknad;
    }

    @GetMapping(value = "/ping")
    @Unprotected
    public String ping(@RequestParam(name = "navn", defaultValue = "earthling") String navn) {
        LOG.info("Jeg ble pinget");
        return "Hallo " + navn + " fra ubeskyttet ressurs";
    }

    @GetMapping(value = "/ping1")
    public String ping1(@RequestParam(name = "navn", defaultValue = "earthling") String navn) {
        LOG.info("Jeg ble pinget");
        return "Hallo " + navn + " fra beskyttet ressurs";
    }

    @GetMapping(value = "/saker")
    public List<FPInfoSakStatus> saker() {
        return saksClient.hentSaker(aktørId());
    }

    private String aktørId() {
        return oppslag.getAktørId().getId();
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
