package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.innsyn.InnsynControllerV2.PATH;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import no.nav.foreldrepenger.common.innsyn.AnnenPartVedtak;
import no.nav.foreldrepenger.common.innsyn.Saker;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.oppslag.OppslagTjeneste;

@ProtectedRestController(PATH)
public class InnsynControllerV2 {

    public static final String PATH = "/innsyn/v2";
    private final OppslagTjeneste oppslag;
    private final Innsyn innsyn;

    public InnsynControllerV2(Innsyn innsyn, OppslagTjeneste oppslag) {
        this.innsyn = innsyn;
        this.oppslag = oppslag;
    }

    @GetMapping("/saker")
    public Saker saker() {
        return innsyn.saker(oppslag.aktørId());
    }

    @PostMapping("/annenPartVedtak")
    public AnnenPartVedtak annenPartVedtak(@Valid @RequestBody AnnenPartVedtakIdentifikator annenPartVedtakIdentifikator) {
        return innsyn.annenPartVedtak(oppslag.aktørId(), annenPartVedtakIdentifikator).orElse(null);
    }
}
