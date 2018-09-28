package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fordeling;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Overføringsårsak;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.Innsyn;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.SakStatus;
import no.nav.foreldrepenger.mottak.util.EnvUtil;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.api.Unprotected;

@RestController
@RequestMapping(path = SøknadController.INNSENDING, produces = APPLICATION_JSON_VALUE)
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class SøknadController {

    private static final Logger LOG = LoggerFactory.getLogger(SøknadController.class);

    @Inject
    Environment env;

    public static final String INNSENDING = "/mottak";

    private final Innsyn innsyn;
    private final Oppslag oppslag;
    private final SøknadSender sender;

    public SøknadController(@Qualifier("dual") SøknadSender sender, Oppslag oppslag, Innsyn innsyn) {
        this.sender = sender;
        this.oppslag = oppslag;
        this.innsyn = innsyn;
    }

    @PostMapping(value = "/send")
    public Kvittering send(@Valid @RequestBody Søknad søknad) {
        return sender.send(søknad, oppslag.getSøker());
    }

    @PostMapping(value = "/ettersend")
    public Kvittering send(@Valid @RequestBody Ettersending ettersending) {
        return sender.send(ettersending, oppslag.getSøker());
    }

    @PostMapping(value = "/endre")
    public Kvittering send(@Valid @RequestBody Endringssøknad endringsSøknad) {
        return sender.send(endringsSøknad, oppslag.getSøker());
    }

    @GetMapping(value = "/ping")
    @Unprotected
    public String ping(@RequestParam(name = "navn", defaultValue = "jordboer") String navn) {
        LOG.info("Jeg ble pinget");
        return "Hallo " + navn + " fra ubeskyttet ressurs";
    }

    @GetMapping(value = "/saker")
    public List<SakStatus> saker() {
        List<SakStatus> saker = innsyn.hentSaker(oppslag.getAktørId());
        if (EnvUtil.isDevOrPreprod(env)) {
            try {
                if (!saker.isEmpty()) {
                    String saksnummer = saker.get(0).getSaksnummer();
                    LOG.trace(EnvUtil.CONFIDENTIAL, "Tester endringssøknad mot sak {}", saksnummer);
                    ValgfrittVedlegg vedlegg = new ValgfrittVedlegg(DokumentType.I500005,
                            new ClassPathResource("sykkel.pdf"));
                    Endringssøknad es = new Endringssøknad(søker(), fordeling(), null, null, null,
                            saksnummer,
                            vedlegg);
                    sender.send(es, oppslag.getSøker());
                }
                else {
                    LOG.trace("Ingen saker å ettersende til");
                }
            } catch (IOException e) {
                LOG.error("Funkade inte, men dette var bare en test", e);
            }
        }
        return saker;

    }

    private static Søker søker() {
        return new Søker(BrukerRolle.MOR);
    }

    private Fordeling fordeling() {
        return new Fordeling(true, Overføringsårsak.ALENEOMSORG, perioder());
    }

    private List<LukketPeriodeMedVedlegg> perioder() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [sender=" + sender + ", innsynTjeneste=" + innsyn + ", oppslag=" + oppslag + "]";
    }

}
