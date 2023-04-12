package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.common.innsyn.AnnenPartVedtak;
import no.nav.foreldrepenger.common.innsyn.Saker;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import static no.nav.foreldrepenger.mottak.innsyn.InnsynControllerV2.PATH;

@ProtectedRestController(PATH)
public class InnsynControllerV2 {

    public static final String PATH = "/innsyn/v2";
    private final Oppslag oppslag;
    private final Innsyn innsyn;

    public InnsynControllerV2(Innsyn innsyn, Oppslag oppslag) {
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
