package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import javax.validation.Valid;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.mappers.DelegerendeDomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.security.oidc.api.Unprotected;

@Unprotected
@RestController
@RequestMapping(path = SøknadPreprodController.INNSENDING_PREPROD, produces = APPLICATION_XML_VALUE)
@Profile(PREPROD)
public class SøknadPreprodController {

    public static final String INNSENDING_PREPROD = "/preprod";

    private final DelegerendeDomainMapper fpDomainMapper;

    public SøknadPreprodController(DelegerendeDomainMapper fpDomainMapper) {
        this.fpDomainMapper = fpDomainMapper;
    }

    @PostMapping("/søknad")
    public String FPsøknadV1(@Valid @RequestBody Søknad søknad) {
        return fpSøknad(søknad, V1);
    }

    @GetMapping(value = "/test", produces = APPLICATION_JSON_VALUE)
    public AktorId test() {
        return new AktorId("42");
    }

    @PostMapping("/søknadV2")
    public String FPsøknadV2(@Valid @RequestBody Søknad søknad) {
        return fpSøknad(søknad, V2);
    }

    @PostMapping("/endringssøknad")
    public String FPendringssøknadV1(@Valid @RequestBody Endringssøknad endringssøknad) {
        return fpEndringsSøknad(endringssøknad, V1);
    }

    @PostMapping("/endringssøknadV2")
    public String FPendringssøknadV2(@Valid @RequestBody Endringssøknad endringssøknad) {
        return fpEndringsSøknad(endringssøknad, V2);
    }

    private String fpSøknad(Søknad søknad, Versjon v) {
        return fpDomainMapper.tilXML(søknad, new AktorId("42"),
                new SøknadEgenskap(v, INITIELL_FORELDREPENGER));
    }

    private String fpEndringsSøknad(Endringssøknad endringssøknad, Versjon v) {
        return fpDomainMapper.tilXML(endringssøknad, new AktorId("42"),
                new SøknadEgenskap(v, ENDRING_FORELDREPENGER));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fpDomainMapper=" + fpDomainMapper + "]";
    }
}
