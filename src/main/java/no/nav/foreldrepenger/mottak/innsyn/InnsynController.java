package no.nav.foreldrepenger.mottak.innsyn;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Sak;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.Uttaksplan;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@ProtectedRestController(InnsynController.INNSYN)
public class InnsynController {

    public static final String INNSYN = "/innsyn";

    private final Oppslag oppslag;
    private final Innsyn innsyn;
    private final ArbeidsInfo arbeidsforhold;

    public InnsynController(Innsyn innsyn, Oppslag oppslag, ArbeidsInfo arbeidsforhold) {
        this.innsyn = innsyn;
        this.oppslag = oppslag;
        this.arbeidsforhold = arbeidsforhold;
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [innsyn=" + innsyn + ", oppslag=" + oppslag + "]";
    }
}
