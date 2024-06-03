package no.nav.foreldrepenger.mottak.oppslag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.http.TokenUtil;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;

@ProtectedRestController(OppslagController.OPPSLAG_PATH)
public class OppslagController {

    public static final Logger LOG = LoggerFactory.getLogger(OppslagController.class);

    public static final String OPPSLAG_PATH = "/oppslag";

    private final PDLConnection pdl;
    private final ArbeidsInfo arbeidsforhold;
    private final TokenUtil tokenUtil;

    public OppslagController(PDLConnection pdl, ArbeidsInfo arbeidsforhold, TokenUtil tokenUtil) {
        this.pdl = pdl;
        this.arbeidsforhold = arbeidsforhold;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping("/aktoer")
    public AktørId aktør() {
        return pdl.aktørId(fnr());
    }

    @GetMapping("/person")
    public Person person() {
        return pdl.hentPerson(fnr());
    }

    @GetMapping("/person/arbeidsforhold")
    public List<EnkeltArbeidsforhold> arbeidsforhold() {
        return arbeidsforhold.hentArbeidsforhold();
    }

    private Fødselsnummer fnr() {
        return tokenUtil.autentisertBrukerOrElseThrowException();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdl=" + pdl + ", tokenUtil=" + tokenUtil + "]";
    }

}
