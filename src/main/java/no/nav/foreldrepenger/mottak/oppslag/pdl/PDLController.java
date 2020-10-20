package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.springframework.web.bind.annotation.GetMapping;

import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.SøkerDTO;

@ProtectedRestController(PDLController.PDL)
public class PDLController {

    public static final String PDL = "/pdl";

    private final PDLTjeneste pdl;

    public PDLController(PDLTjeneste pdl) {
        this.pdl = pdl;
    }

    @GetMapping("/person")
    public SøkerDTO person() {
        return pdl.person();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdl=" + pdl + "]";
    }
}
