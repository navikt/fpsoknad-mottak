package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.AbstractInspektør.SØKNAD;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ENDRINGSSØKNAD;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.FØRSTEGANGSSØKNAD;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.INITIELL_SVANGERSKAPSPENGER;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.innsending.varsel.Varsel;
import no.nav.foreldrepenger.mottak.innsending.varsel.Varsler;
import no.nav.foreldrepenger.mottak.innsyn.Innsyn;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.security.token.support.core.api.Unprotected;

@ProtectedRestController(MottakController.INNSENDING)
public class MottakController {
    private static final Logger LOG = LoggerFactory.getLogger(MottakController.class);
    public static final String INNSENDING = "/mottak";
    private final Innsyn innsyn;
    private final Oppslag oppslag;
    private final SøknadSender søknadSender;
    private final Inspektør inspektør;
    private final Varsler varsler;

    public MottakController(SøknadSender søknadSender, Varsler varsler, Oppslag oppslag, Innsyn innsyn,
            @Qualifier(SØKNAD) Inspektør inspektør) {
        this.søknadSender = søknadSender;
        this.oppslag = oppslag;
        this.innsyn = innsyn;
        this.inspektør = inspektør;
        this.varsler = varsler;
    }

    @PostMapping("/send")
    public Kvittering initiell(@Valid @RequestBody Søknad søknad) {
        var søknadEgenskap = inspektør.inspiser(søknad);
        return sjekkStatus(søknadSender.søk(søknad, oppslag.person(), søknadEgenskap),
                FØRSTEGANGSSØKNAD, varsleHvisVellykket(søknadEgenskap));
    }

    @PostMapping("/ettersend")
    public Kvittering ettersend(@Valid @RequestBody Ettersending ettersending) {
        return sjekkStatus(søknadSender.ettersend(ettersending, oppslag.person(),
                inspektør.inspiser(ettersending)), "Ettersending", false);
    }

    @PostMapping("/endre")
    public Kvittering endre(@Valid @RequestBody Endringssøknad endringssøknad) {
        return sjekkStatus(søknadSender.endreSøknad(endringssøknad, oppslag.person(), ENDRING_FORELDREPENGER),
                ENDRINGSSØKNAD);
    }

    @GetMapping("/ping")
    @Unprotected
    public String ping(@RequestParam(name = "navn", defaultValue = "jordboer") String navn) {
        LOG.info("Jeg ble pinget");
        return "Hallo " + navn + " fra ubeskyttet ressurs";
    }

    @GetMapping("/saker")
    public List<Sak> saker() {
        return innsyn.saker(oppslag.aktørId());
    }

    private static boolean varsleHvisVellykket(SøknadEgenskap søknadEgenskap) {
        return søknadEgenskap != INITIELL_SVANGERSKAPSPENGER;
    }

    private Kvittering sjekkStatus(Kvittering kvittering, String type) {
        return sjekkStatus(kvittering, type, true);
    }

    private Kvittering sjekkStatus(Kvittering kvittering, String type, boolean varsle) {
        if (!kvittering.erVellykket()) {
            LOG.warn("{} fikk ikke entydig status ({}), dette bør sjekkes opp nærmere", type,
                    kvittering.getLeveranseStatus());
        }
        if (varsle && kvittering.erVellykket()) {
            varsler.varsle(varselFra(kvittering));
        }
        return kvittering;
    }

    private Varsel varselFra(Kvittering kvittering) {
        return new Varsel(kvittering.getMottattDato(), oppslag.person());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [innsyn=" + innsyn + ", oppslag=" + oppslag + ", søknadSender="
                + søknadSender + ", inspektør=" + inspektør + "]";
    }
}
