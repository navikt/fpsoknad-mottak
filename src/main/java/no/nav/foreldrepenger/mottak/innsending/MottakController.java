package no.nav.foreldrepenger.mottak.innsending;

import jakarta.validation.Valid;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.security.token.support.core.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static no.nav.foreldrepenger.common.innsending.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadValidator.validerFpSøknad;
import static no.nav.foreldrepenger.mottak.innsending.SøknadValidator.validerFørstegangFpSøknad;

@ProtectedRestController(MottakController.INNSENDING)
public class MottakController {
    private static final Logger LOG = LoggerFactory.getLogger(MottakController.class);
    public static final String INNSENDING = "/mottak";
    private final Oppslag oppslag;
    private final SøknadSender søknadSender;

    public MottakController(SøknadSender søknadSender,
                            Oppslag oppslag) {
        this.søknadSender = søknadSender;
        this.oppslag = oppslag;
    }

    @PostMapping("/send")
    public Kvittering initiell(@Valid @RequestBody Søknad søknad) {
        var søknadEgenskap = Inspektør.inspiser(søknad);
        validerFørstegangFpSøknad(søknad);
        return søknadSender.søk(søknad, oppslag.person(), søknadEgenskap);
    }

    @PostMapping("/ettersend")
    public Kvittering ettersend(@Valid @RequestBody Ettersending ettersending) {
        return søknadSender.ettersend(ettersending, oppslag.person(), Inspektør.inspiser(ettersending));
    }

    @PostMapping("/endre")
    public Kvittering endre(@Valid @RequestBody Endringssøknad endringssøknad) {
        validerFpSøknad(endringssøknad.getYtelse());
        return søknadSender.endreSøknad(endringssøknad, oppslag.person(), ENDRING_FORELDREPENGER);
    }

    @Unprotected
    @GetMapping("/ping")
    public String ping() {
        LOG.info("Jeg ble pinget");
        return "pong";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", søknadSender="
                + søknadSender +"]";
    }


}
