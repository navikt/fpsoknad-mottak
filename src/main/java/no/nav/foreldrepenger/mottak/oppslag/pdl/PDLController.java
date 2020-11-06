package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.springframework.web.bind.annotation.GetMapping;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdTjeneste;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.SøkerDTO;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.SøkerinfoDTO;

@ProtectedRestController(PDLController.PDL)
public class PDLController {

    public static final String PDL = "/pdl";

    private final PDLTjeneste pdl;
    private final ArbeidsforholdTjeneste arbeidsforhold;

    public PDLController(PDLTjeneste pdl, ArbeidsforholdTjeneste arbeidsforhold) {
        this.pdl = pdl;
        this.arbeidsforhold = arbeidsforhold;
    }

    @GetMapping("/person")
    public SøkerDTO søker() {
        return pdl.søker();
    }

    @GetMapping("/navn")
    public Navn navn() {
        return pdl.navn();
    }

    @GetMapping("/sokerinfo")
    public SøkerinfoDTO søkerinfo() {
        return new SøkerinfoDTO(søker(), arbeidsforhold.hentAktiveArbeidsforhold());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdl=" + pdl + ", arbeidsforhold=" + arbeidsforhold + "]";
    }

}
