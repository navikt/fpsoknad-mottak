package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.common.util.Constants.FORELDREPENGER;

import java.util.List;

import no.nav.foreldrepenger.mottak.innsyn.fpinfov2.Saker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Sak;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.Uttaksplan;
import no.nav.foreldrepenger.common.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;
import no.nav.foreldrepenger.mottak.oppslag.sak.SakClient;

@ProtectedRestController(InnsynController.INNSYN)
public class InnsynController {

    public static final String SAK = "/sak1";

    public static final String INNSYN = "/innsyn";

    private final Oppslag oppslag;
    private final Innsyn innsyn;
    private final ArbeidsInfo arbeidsforhold;
    private final SakClient sakClient;

    public InnsynController(Innsyn innsyn, Oppslag oppslag, ArbeidsInfo arbeidsforhold, SakClient sakClient) {
        this.innsyn = innsyn;
        this.oppslag = oppslag;
        this.arbeidsforhold = arbeidsforhold;
        this.sakClient = sakClient;
    }

    @GetMapping(SAK)
    public List<no.nav.foreldrepenger.mottak.oppslag.sak.Sak> saker(@RequestParam(name = "tema", defaultValue = FORELDREPENGER) String tema) {
        return sakClient.sakerFor(oppslag.aktørId(), tema);
    }

    @GetMapping("/saker")
    public List<Sak> saker() {
        return innsyn.saker(oppslag.aktørId());
    }

    @GetMapping("/arbeidsforhold")
    public List<EnkeltArbeidsforhold> arbeidsforhold() {
        return arbeidsforhold.hentArbeidsforhold();
    }

    @GetMapping("/orgnavn")
    public String orgnavn(@RequestParam(name = "orgnr") String orgnr) {
        return arbeidsforhold.orgnavn(orgnr);
    }

    @GetMapping("/uttaksplan")
    public Uttaksplan uttaksplan(@RequestParam(name = "saksnummer") String saksnummer) {
        return innsyn.uttaksplan(saksnummer);
    }

    @GetMapping("/uttaksplanannen")
    public Uttaksplan uttaksplan(@RequestParam(name = "annenPart") Fødselsnummer annenPart) {
        return innsyn.uttaksplan(oppslag.aktørId(), oppslag.aktørId(annenPart));
    }

    @GetMapping("/vedtak")
    public Vedtak vedtak(@RequestParam(name = "saksnummer") String saksnummer) {
        return innsyn.vedtak(oppslag.aktørId(), saksnummer);
    }

    @GetMapping("/v2/saker")
    public Saker sakerV2() {
        return innsyn.sakerV2(oppslag.aktørId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [innsyn=" + innsyn + ", oppslag=" + oppslag + "]";
    }
}
