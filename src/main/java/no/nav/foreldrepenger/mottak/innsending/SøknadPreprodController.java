package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.AbstractInspektør.SØKNAD;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;
import no.nav.security.oidc.api.Unprotected;

@Unprotected
@RestController
@RequestMapping(path = SøknadPreprodController.INNSENDING_PREPROD, produces = APPLICATION_XML_VALUE)
@Profile(PREPROD)
public class SøknadPreprodController {

    private static final AktorId SØKER = new AktorId("42");

    public static final String INNSENDING_PREPROD = "/preprod";

    private final DomainMapper domainMapper;
    private final Inspektør inspektør;

    public SøknadPreprodController(@Qualifier(DELEGERENDE) DomainMapper domainMapper,
            @Qualifier(SØKNAD) Inspektør inspektør) {
        this.domainMapper = domainMapper;
        this.inspektør = inspektør;
    }

    @PostMapping("/søknad")
    public String FPsøknad(@Valid @RequestBody Søknad søknad) {
        return fpSøknad(søknad);
    }

    @GetMapping(value = "/test", produces = APPLICATION_JSON_VALUE)
    public AktorId test() {
        return SØKER;
    }

    @PostMapping("/endringssøknad")
    public String FPendringssøknad(@Valid @RequestBody Endringssøknad endringssøknad) {
        return fpEndringsSøknad(endringssøknad);
    }

    private String fpSøknad(Søknad søknad) {
        return domainMapper.tilXML(søknad, SØKER, inspektør.inspiser(søknad));
    }

    private String fpEndringsSøknad(Endringssøknad endringssøknad) {
        return domainMapper.tilXML(endringssøknad, SØKER, inspektør.inspiser(endringssøknad));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [domainMapper=" + domainMapper + "]";
    }
}
