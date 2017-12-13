package no.nav.foreldrepenger.selvbetjening.openam.exception;


public class CookieNotPresentException extends UnAuthorizedException {

    public CookieNotPresentException(String message) {
        super(message);
    }

}
