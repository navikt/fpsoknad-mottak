package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.AbstractInspektør.SØKNAD;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Sak;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.innsending.varsel.Varsel;
import no.nav.foreldrepenger.mottak.innsending.varsel.VarselSender;
import no.nav.foreldrepenger.mottak.innsyn.Innsyn;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;
import no.nav.security.token.support.core.api.Unprotected;

@ProtectedRestController(MottakController.INNSENDING)
public class MottakController {
    private static final Logger LOG = LoggerFactory.getLogger(MottakController.class);
    public static final String INNSENDING = "/mottak";
    private final Innsyn innsyn;
    private final Oppslag oppslag;
    private final SøknadSender søknadSender;
    private final Inspektør inspektør;
    private final VarselSender varselSender;

    public MottakController(SøknadSender søknadSender,
                            Oppslag oppslag,
                            Innsyn innsyn,
                            @Qualifier(SØKNAD) Inspektør inspektør,
                            VarselSender varselSender) {
        this.søknadSender = søknadSender;
        this.oppslag = oppslag;
        this.innsyn = innsyn;
        this.inspektør = inspektør;
        this.varselSender = varselSender;
    }

    @PostMapping("/send")
    public Kvittering initiell(@Valid @RequestBody Søknad søknad) {
        var søknadEgenskap = inspektør.inspiser(søknad);
        var kvittering = søknadSender.søk(søknad, oppslag.person(), søknadEgenskap);
        return sendVarsel(kvittering, skalVarsle(søknadEgenskap));
    }

    @PostMapping("/ettersend")
    public Kvittering ettersend(@Valid @RequestBody Ettersending ettersending) {
        var kvittering = søknadSender.ettersend(ettersending, oppslag.person(), inspektør.inspiser(ettersending));
        return sendVarsel(kvittering, false);
    }

    @PostMapping("/endre")
    public Kvittering endre(@Valid @RequestBody Endringssøknad endringssøknad) {
        var kvittering = søknadSender.endreSøknad(endringssøknad, oppslag.person(), ENDRING_FORELDREPENGER);
        return sendVarsel(kvittering);
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

    private static boolean skalVarsle(SøknadEgenskap søknadEgenskap) {
        return søknadEgenskap != INITIELL_SVANGERSKAPSPENGER;
    }

    private Kvittering sendVarsel(Kvittering kvittering) {
        return sendVarsel(kvittering, true);
    }

    private Kvittering sendVarsel(Kvittering kvittering, boolean varsle) {
        if (varsle && erVellykket(kvittering)) {
            varselSender.varsle(varselFra(kvittering));
        }
        return kvittering;
    }

    private static boolean erVellykket(Kvittering kvittering) {
        //Hvis saksnummer er null så har ikke fpfordel gitt et entydig svar
        return kvittering.getSaksNr() != null;
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
