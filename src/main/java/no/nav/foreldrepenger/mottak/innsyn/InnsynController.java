package no.nav.foreldrepenger.mottak.innsyn;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;

@Deprecated // Brukes av fpinfo-historikk til å lage infoskriv om AGs IM
@ProtectedRestController(InnsynController.PATH)
public class InnsynController {

    public static final String PATH = "/innsyn";

    private final ArbeidsInfo arbeidsforhold;

    public InnsynController(ArbeidsInfo arbeidsforhold) {
        this.arbeidsforhold = arbeidsforhold;
    }

    @GetMapping("/orgnavn")
    public String orgnavn(@Valid @RequestParam(name = "orgnr") Orgnummer orgnr) {
        return arbeidsforhold.orgnavn(orgnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [arbeidsforhold=" + arbeidsforhold + "]";
    }
}
