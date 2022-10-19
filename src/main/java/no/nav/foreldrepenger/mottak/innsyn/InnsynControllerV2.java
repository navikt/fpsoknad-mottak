package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.innsyn.InnsynControllerV2.INNSYNV2;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import no.nav.boot.conditionals.ConditionalOnNotProd;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;
import no.nav.foreldrepenger.common.innsyn.v2.VedtakPeriode;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;

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

    @PostMapping("/annenForeldersVedtaksperioder")
    public List<VedtakPeriode> annenPartsVedtaksperioder(@Valid @RequestBody AnnenPartVedtakIdentifikator annenPartVedtakIdentifikator) {
        return innsyn.annenPartsVedtaksperioder(oppslag.aktørId(), annenPartVedtakIdentifikator);
    }
}
