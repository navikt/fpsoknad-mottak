package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.innsyn.InnsynControllerV2.PATH;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsyn.AnnenPartVedtak;
import no.nav.foreldrepenger.common.innsyn.Saker;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;

@ProtectedRestController(PATH)
public class InnsynControllerV2 {

    public static final String PATH = "/innsyn/v2";
    private final PDLConnection pdl;
    private final TokenUtil tokenHelper;
    private final Innsyn innsyn;

    public InnsynControllerV2(Innsyn innsyn, PDLConnection pdl, TokenUtil tokenHelper) {
        this.innsyn = innsyn;
        this.pdl = pdl;
        this.tokenHelper = tokenHelper;
    }

    @GetMapping("/saker")
    public Saker saker() {
        return innsyn.saker(fnr());
    }

    @PostMapping("/annenPartVedtak")
    public AnnenPartVedtak annenPartVedtak(@Valid @RequestBody AnnenPartVedtakIdentifikator annenPartVedtakIdentifikator) {
        return innsyn.annenPartVedtak(aktørId(), annenPartVedtakIdentifikator).orElse(null);
    }

    private AktørId aktørId() {
        var fnr = fnr();
        return pdl.aktørId(fnr);
    }

    private Fødselsnummer fnr() {
        return tokenHelper.autentisertBrukerOrElseThrowException();
    }
}
