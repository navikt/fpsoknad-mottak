package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.http.UnprotectedRestController;

@UnprotectedRestController("/pdl/dev")
public class PDLDevController {

    private final PDLTjeneste pdl;

    public PDLDevController(PDLTjeneste pdl) {
        this.pdl = pdl;
    }

    @GetMapping("/navn")
    public Navn navn(@RequestParam(name = "fnr") Fødselsnummer fnr) {
        return pdl.navn(fnr.getFnr());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdl=" + pdl + "]";
    }
}
