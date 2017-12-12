package no.nav.foreldrepenger.selvbetjening.security.openam.util;

import static org.springframework.util.StringUtils.hasText;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import no.nav.foreldrepenger.selvbetjening.security.openam.exception.CookieNotPresentException;
import no.nav.foreldrepenger.selvbetjening.security.openam.exception.MissingCookieException;

public class EssoCookieUtil {

    private EssoCookieUtil() {
    }

    public static final String ESSO_COOKIE_NAME = "nav-esso";
    public static final String ESSO_VALUE_ANONYMOUS = "ANONYMOUS_USER";
    public static final String INVALID_ESSO_COOKIE = "invalid-" + ESSO_COOKIE_NAME;

    public static String verifyEssoCookiePresentAndGetValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new CookieNotPresentException("Could not find any cookies on request " + request.getRequestURI());
        }

        String value = getEssoCookie(cookies);
        if (!hasText(value)) {
            throw new CookieNotPresentException("No cookie with name: " + ESSO_COOKIE_NAME + " on " + request.getRequestURI());
        }

        return value;
    }

    public static String getEssoCookieValueIfPresentAndValidOrSetAnonymous(HttpServletRequest request) {
        try {
            String cookieValue = verifyEssoCookiePresentAndGetValue(request);
            String invalidCookie = (String) request.getAttribute(INVALID_ESSO_COOKIE);
            if (invalidCookie != null && invalidCookie.equals(cookieValue)) {
                return ESSO_VALUE_ANONYMOUS;
            }
            return cookieValue;
        } catch (CookieNotPresentException e) {
            return ESSO_VALUE_ANONYMOUS;
        }
    }

    public static String verifyEssoCookieWithException(HttpServletRequest request) throws MissingCookieException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new MissingCookieException("Could not find any cookies on request " + request.getRequestURI());
        }

        String value = getEssoCookie(cookies);

        if (!hasText(value)) {
            throw new MissingCookieException("No cookie with name: " + ESSO_COOKIE_NAME + " on " + request.getRequestURI());
        }

        return value;
    }

    public static String getEssoCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(ESSO_COOKIE_NAME)) {
                return cookie.getValue();
            }
        }
        return "";
    }
}
