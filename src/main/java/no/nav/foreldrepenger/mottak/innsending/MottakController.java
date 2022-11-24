package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.ENDRING_FORELDREPENGER;

import java.util.Objects;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.Ytelse;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.security.token.support.core.api.Unprotected;

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

    private void validerFørstegangFpSøknad(Søknad søknad) {
        var ytelse = søknad.getYtelse();
        validerFpSøknad(ytelse);
        if (ytelse instanceof Foreldrepenger foreldrepenger) {
            var perioder = foreldrepenger.fordeling().perioder();
            //Allerede validert på minst en periode
            if (perioder.stream().allMatch(this::erFriUtsettelse)) {
                throw new UnexpectedInputException(
                    "Søknad må inneholde minst en søknadsperiode som ikke" + "er fri utsettelse");
            }
        }
    }

    private boolean erFriUtsettelse(LukketPeriodeMedVedlegg p) {
        return p instanceof UtsettelsesPeriode utsettelsesPeriode && Objects.equals(utsettelsesPeriode.getÅrsak(),
            UtsettelsesÅrsak.FRI);
    }

    private void validerFpSøknad(Ytelse ytelse) {
        if (ytelse instanceof Foreldrepenger foreldrepenger && foreldrepenger.fordeling().perioder().isEmpty()) {
            throw new UnexpectedInputException("Søknad må inneholde minst en søknadsperiode");
        }
    }
}
