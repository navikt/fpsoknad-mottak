package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ETTERSENDING_FORELDREPENGER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.innsyn.Innsyn;
import no.nav.foreldrepenger.mottak.innsyn.SøknadInspektør;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.api.Unprotected;

@RequestMapping(path = SøknadController.INNSENDING, produces = APPLICATION_JSON_VALUE)
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
@RestController
public class SøknadController {

    private static final Logger LOG = LoggerFactory.getLogger(SøknadController.class);

    public static final String INNSENDING = "/mottak";

    private final Innsyn innsyn;
    private final Oppslag oppslag;
    private final SøknadSender sender;
    private final SøknadInspektør inspektør;

    public SøknadController(SøknadSender sender, Oppslag oppslag, Innsyn innsyn, SøknadInspektør inspektør) {
        this.sender = sender;
        this.oppslag = oppslag;
        this.innsyn = innsyn;
        this.inspektør = inspektør;
    }

    @PostMapping("/send")
    public Kvittering send(@Valid @RequestBody Søknad søknad) {
        return sender.send(søknad, oppslag.getSøker(), inspektør.inspiser(søknad));
    }

    @PostMapping("/ettersend")
    public Kvittering send(@Valid @RequestBody Ettersending ettersending) {
        return sender.send(ettersending, oppslag.getSøker(), ETTERSENDING_FORELDREPENGER);
    }

    @PostMapping("/endre")
    public Kvittering send(@Valid @RequestBody Endringssøknad endringssøknad) {
        return sender.send(endringssøknad, oppslag.getSøker(), ENDRING_FORELDREPENGER);
    }

    @GetMapping("/ping")
    @Unprotected
    public String ping(@RequestParam(name = "navn", defaultValue = "jordboer") String navn) {
        LOG.info("Jeg ble pinget");
        return "Hallo " + navn + " fra ubeskyttet ressurs";
    }

    @GetMapping(value = "/saker")
    public List<Sak> saker() {
        return innsyn.hentSaker(oppslag.getAktørId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [innsyn=" + innsyn + ", oppslag=" + oppslag + ", sender=" + sender
                + ", inspektør=" + inspektør + "]";
    }
}
