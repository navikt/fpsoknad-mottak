package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.innsyn.InnsynControllerV2.INNSYNV2;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.boot.conditionals.Cluster;
import no.nav.boot.conditionals.ConditionalOnClusters;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;
import no.nav.foreldrepenger.common.innsyn.v2.VedtakPeriode;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;

@ConditionalOnClusters(clusters = {Cluster.DEV_FSS, Cluster.LOCAL, Cluster.VTP, Cluster.TEST})
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

    @GetMapping("/annenForeldersVedtaksperioder")
    public List<VedtakPeriode> annenPartsVedtaksperioder(@Valid @RequestParam("annenPartAktorId") AktørId annenPartAktørId,
                                                         @Valid @RequestParam("barnAktorId") AktørId barnAktorId) {
        return innsyn.annenPartsVedtaksperioder(oppslag.aktørId(), annenPartAktørId, barnAktorId);
    }
}
