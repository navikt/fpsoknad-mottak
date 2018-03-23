package no.nav.foreldrepenger.mottak.http;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.security.spring.oidc.validation.api.Unprotected;

@RestController
@Unprotected
public class MottakErrorController implements ErrorController {
    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "Gikk galt, dette gitt.., har du logget på ?";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

}
