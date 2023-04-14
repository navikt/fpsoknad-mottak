package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.common.innsending.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadValidator.validerFpSøknad;
import static no.nav.foreldrepenger.mottak.innsending.SøknadValidator.validerFørstegangFpSøknad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;
import no.nav.security.token.support.core.api.Unprotected;

@ProtectedRestController(MottakController.INNSENDING)
public class MottakController {
    private static final Logger LOG = LoggerFactory.getLogger(MottakController.class);
    public static final String INNSENDING = "/mottak";
    private final PDLConnection pdl;
    private final SøknadSender søknadSender;
    private final TokenUtil tokenUtil;

    public MottakController(SøknadSender søknadSender,
                            PDLConnection pdl,
                            TokenUtil tokenUtil) {
        this.søknadSender = søknadSender;
        this.pdl = pdl;
        this.tokenUtil = tokenUtil;
    }

    @PostMapping("/send")
    public Kvittering initiell(@Valid @RequestBody Søknad søknad) {
        var søknadEgenskap = Inspektør.inspiser(søknad);
        validerFørstegangFpSøknad(søknad);
        var innsendingPersonInfo = personInfo();
        return søknadSender.søk(søknad, søknadEgenskap, innsendingPersonInfo);
    }

    private InnsendingPersonInfo map(Person person) {
        return new InnsendingPersonInfo(person.navn(), person.aktørId(), person.fnr());
    }

    @PostMapping("/ettersend")
    public Kvittering ettersend(@Valid @RequestBody Ettersending ettersending) {
        var innsendingPersonInfo = personInfo();
        return søknadSender.ettersend(ettersending, Inspektør.inspiser(ettersending), innsendingPersonInfo);
    }

    @PostMapping("/endre")
    public Kvittering endre(@Valid @RequestBody Endringssøknad endringssøknad) {
        validerFpSøknad(endringssøknad.getYtelse());
        var innsendingPersonInfo = personInfo();
        return søknadSender.endreSøknad(endringssøknad, ENDRING_FORELDREPENGER, innsendingPersonInfo);
    }

    private InnsendingPersonInfo personInfo() {
        //TODO erstatte med et enklere pdl oppslag
        var fnr = tokenUtil.autentisertBrukerOrElseThrowException();
        var person = pdl.hentPerson(fnr);
        return map(person);
    }

    @Unprotected
    @GetMapping("/ping")
    public String ping() {
        LOG.info("Jeg ble pinget");
        return "pong";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdl=" + pdl + ", søknadSender="
                + søknadSender +"]";
    }


}
