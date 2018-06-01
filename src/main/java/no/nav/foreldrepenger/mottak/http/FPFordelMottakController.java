package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelSøknadSender;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;

@RestController
@RequestMapping(path = FPFordelMottakController.FPFORDEL, produces = APPLICATION_JSON_VALUE)
public class FPFordelMottakController {

    public static final String FPFORDEL = "/mottak/fpfordel";

    private final FPFordelSøknadSender sender;
    private final AktørIDLookup aktørIdService;

    public FPFordelMottakController(FPFordelSøknadSender sender, AktørIDLookup aktørIdService) {
        this.sender = sender;
        this.aktørIdService = aktørIdService;
    }

    @PostMapping(value = "/send")
    @ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
    public ResponseEntity<Kvittering> send(@Valid @RequestBody Søknad søknad) {
        return ok(sender.sendSøknad(søknad, aktørIdService.getAktørId()));
    }

    @PostMapping(value = "/ettersend")
    @ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
    public ResponseEntity<Kvittering> send(@Valid @RequestBody Ettersending ettersending) {
        return ok(sender.sendEttersending(ettersending, aktørIdService.getAktørId()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sender=" + sender + ", aktørIdService=" + aktørIdService + "]";
    }

}
