package no.nav.foreldrepenger.mottak.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.security.spring.oidc.validation.api.Unprotected;

@RestController
@Unprotected
public class MottakErrorController implements ErrorController {

    @Autowired
    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error(HttpServletRequest request, HttpServletResponse response) {
        return "Gikk galt dette her, gitt. Responskode " + response.getStatus() + ", har du logget på ?";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

}
