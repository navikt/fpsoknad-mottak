package no.nav.foreldrepenger.selvbetjening.security.openam.exception;


public class CookieNotPresentException extends UnAuthorizedException {

    public CookieNotPresentException(String message) {
        super(message);
    }

}
