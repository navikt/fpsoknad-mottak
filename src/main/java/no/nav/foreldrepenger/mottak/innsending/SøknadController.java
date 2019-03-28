package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.AbstractXMLInspektør.SØKNAD;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.varsel.Varsel;
import no.nav.foreldrepenger.mottak.innsending.varsel.VarselSender;
import no.nav.foreldrepenger.mottak.innsyn.Innsyn;
import no.nav.foreldrepenger.mottak.innsyn.XMLInspektør;
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
    private final SøknadSender søknadSender;
    private final XMLInspektør inspektør;
    private final VarselSender varselSender;

    public SøknadController(SøknadSender søknadSender, VarselSender varselSender, Oppslag oppslag, Innsyn innsyn,
            @Qualifier(SØKNAD) XMLInspektør inspektør) {
        this.søknadSender = søknadSender;
        this.oppslag = oppslag;
        this.innsyn = innsyn;
        this.inspektør = inspektør;
        this.varselSender = varselSender;
    }

    @PostMapping("/send")
    public Kvittering initiell(@Valid @RequestBody Søknad søknad) {
        return sjekkStatus(søknadSender.søk(søknad, oppslag.getSøker(), inspektør.inspiser(søknad)),
                "Førstegangssøknad");
    }

    @PostMapping("/ettersend")
    public Kvittering ettersend(@Valid @RequestBody Ettersending ettersending) {
        return sjekkStatus(søknadSender.ettersend(ettersending, oppslag.getSøker(),
                inspektør.inspiser(ettersending)), "Ettersending", false);
    }

    @PostMapping("/endre")
    public Kvittering endre(@Valid @RequestBody Endringssøknad endringssøknad) {
        return sjekkStatus(søknadSender.endreSøknad(endringssøknad, oppslag.getSøker(), ENDRING_FORELDREPENGER),
                "Endringssøknad");
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

    private Kvittering sjekkStatus(Kvittering kvittering, String type) {
        return sjekkStatus(kvittering, type, true);
    }

    private Kvittering sjekkStatus(Kvittering kvittering, String type, boolean varsle) {
        if (kvittering.erUklarStatus()) {
            LOG.warn("{} fikk ikke entydig status, dette bør sjekkes opp nærmere", type);
        }
        if (varsle && kvittering.erVellykket()) {
            varselSender.varsle(varselFra(kvittering));
        }
        return kvittering;
    }

    private Varsel varselFra(Kvittering kvittering) {
        return new Varsel(kvittering.getMottattDato(), oppslag.getSøker());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [innsyn=" + innsyn + ", oppslag=" + oppslag + ", søknadSender="
                + søknadSender + ", inspektør=" + inspektør + "]";
    }
}
