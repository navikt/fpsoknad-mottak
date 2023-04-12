package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import java.util.List;

@ProtectedRestController(InnsynController.PATH)
public class InnsynController {

    public static final String PATH = "/innsyn";

    private final Innsyn innsyn;
    private final ArbeidsInfo arbeidsforhold;

    public InnsynController(Innsyn innsyn, ArbeidsInfo arbeidsforhold) {
        this.innsyn = innsyn;
        this.arbeidsforhold = arbeidsforhold;
    }

    @GetMapping("/arbeidsforhold")
    public List<EnkeltArbeidsforhold> arbeidsforhold() {
        return arbeidsforhold.hentArbeidsforhold();
    }

    @GetMapping("/orgnavn")
    public String orgnavn(@Valid @RequestParam(name = "orgnr") Orgnummer orgnr) {
        return arbeidsforhold.orgnavn(orgnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [innsyn=" + innsyn + "]";
    }
}
