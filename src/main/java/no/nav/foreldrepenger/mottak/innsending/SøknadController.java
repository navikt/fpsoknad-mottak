package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.innsending.SøknadSender.ROUTING_SENDER;
import static no.nav.foreldrepenger.mottak.innsending.engangsstønad.EngangsstønadDestinasjon.DOKMOT;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ETTERSENDING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.innsending.engangsstønad.EngangsstønadDestinasjon;
import no.nav.foreldrepenger.mottak.innsyn.Innsyn;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.Versjon;
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
    private final EngangsstønadDestinasjon destinasjon;

    public SøknadController(@Qualifier(ROUTING_SENDER) SøknadSender sender, Oppslag oppslag,
            Innsyn innsyn, @Value("${engangs.destinasjon:DOKMOT}") EngangsstønadDestinasjon destinasjon) {
        this.sender = sender;
        this.oppslag = oppslag;
        this.innsyn = innsyn;
        this.destinasjon = destinasjon;
    }

    @PostMapping("/send")
    public Kvittering send(@Valid @RequestBody Søknad søknad) {
        return sender.send(søknad, oppslag.getSøker(), søknadEgenskapFra(søknad));
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

    private SøknadEgenskap søknadEgenskapFra(Søknad søknad) {
        return søknad.getYtelse() instanceof Foreldrepenger
                ? INITIELL_FORELDREPENGER
                : new SøknadEgenskap(versjonFraDestinasjon(), INITIELL_ENGANGSSTØNAD);
    }

    private Versjon versjonFraDestinasjon() {
        return DOKMOT.equals(destinasjon) ? V1 : DEFAULT_VERSJON;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [innsyn=" + innsyn + ", oppslag=" + oppslag + ", sender=" + sender
                + ", destinasjon=" + destinasjon + "]";
    }
}
