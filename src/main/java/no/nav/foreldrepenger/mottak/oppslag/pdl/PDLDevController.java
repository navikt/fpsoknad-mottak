package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.springframework.web.bind.annotation.GetMapping;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.http.UnprotectedRestController;

@UnprotectedRestController(PDLController.PDL + "/dev")
public class PDLDevController {

    private final PDLTjeneste pdl;

    public PDLDevController(PDLTjeneste pdl) {
        this.pdl = pdl;
    }

    @GetMapping("/navn")
    public Navn navn() {
        return pdl.navn();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdl=" + pdl + "]";
    }

}
