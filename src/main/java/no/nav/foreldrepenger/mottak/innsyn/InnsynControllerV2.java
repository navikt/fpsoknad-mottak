package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnNotProd;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.innsyn.fpinfov2.Saker;
import org.springframework.web.bind.annotation.GetMapping;

import static no.nav.foreldrepenger.mottak.innsyn.InnsynControllerV2.*;

@ConditionalOnNotProd
@ProtectedRestController(INNSYNV2)
public class InnsynControllerV2 {
    public static final String INNSYNV2 = "/innsyn/v2";
    private final Oppslag oppslag;
    private final Innsyn innsyn;

    public InnsynControllerV2(Innsyn innsyn, Oppslag oppslag) {
        this.innsyn = innsyn;
        this.oppslag = oppslag;
    }

    @GetMapping("/saker")
    public Saker saker() {
        return innsyn.sakerV2(oppslag.aktørId());
    }
}
