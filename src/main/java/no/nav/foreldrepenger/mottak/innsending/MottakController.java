package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.common.innsending.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadValidator.validerFørstegangssøknad;
import static no.nav.foreldrepenger.mottak.innsending.SøknadValidator.validerSøknad;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.Ytelse.ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.Ytelse.FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.Ytelse.SVANGERSKAPSPENGER;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.felles.EttersendingsType;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;
import no.nav.foreldrepenger.mottak.oppslag.pdl.Ytelse;

@ProtectedRestController(MottakController.INNSENDING)
public class MottakController {
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
        validerFørstegangssøknad(søknad);
        var innsendingPersonInfo = personInfo(tilYtelse(søknad.getYtelse()));
        return søknadSender.søk(søknad, søknadEgenskap, innsendingPersonInfo);
    }

    private InnsendingPersonInfo map(Person person) {
        return new InnsendingPersonInfo(person.navn(), person.aktørId(), person.fnr());
    }

    @PostMapping("/ettersend")
    public Kvittering ettersend(@Valid @RequestBody Ettersending ettersending) {
        var innsendingPersonInfo = personInfo(tilYtelse(ettersending.type()));
        return søknadSender.ettersend(ettersending, Inspektør.inspiser(ettersending), innsendingPersonInfo);
    }

    @PostMapping("/endre")
    public Kvittering endre(@Valid @RequestBody Endringssøknad endringssøknad) {
        validerSøknad(endringssøknad.getYtelse());
        var innsendingPersonInfo = personInfo(tilYtelse(endringssøknad.getYtelse()));
        return søknadSender.endreSøknad(endringssøknad, ENDRING_FORELDREPENGER, innsendingPersonInfo);
    }

    private InnsendingPersonInfo personInfo(Ytelse ytelse) {
        var fnr = tokenUtil.autentisertBrukerOrElseThrowException();
        var person = pdl.hentPerson(fnr, ytelse);
        return map(person);
    }

    private static Ytelse tilYtelse(EttersendingsType ettersendingsType) {
        return switch (ettersendingsType) {
            case ENGANGSSTØNAD -> ENGANGSSTØNAD;
            case FORELDREPENGER -> FORELDREPENGER;
            case SVANGERSKAPSPENGER -> SVANGERSKAPSPENGER;
        };
    }
    private static Ytelse tilYtelse(no.nav.foreldrepenger.common.domain.Ytelse ytelse) {
        if (ytelse instanceof Engangsstønad) {
            return ENGANGSSTØNAD;
        } else if (ytelse instanceof Svangerskapspenger) {
            return SVANGERSKAPSPENGER;
        } else {
            return FORELDREPENGER;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdl=" + pdl + ", søknadSender="
                + søknadSender +"]";
    }


}
