package no.nav.foreldrepenger.selvbetjening.openam.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import no.nav.foreldrepenger.selvbetjening.openam.domain.NavPrincipal;

public class SecurityContextUtil {
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static NavPrincipal getNavPrincipal() {
        return (NavPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static boolean erBrukerPaalogget() {
        Authentication authentication = SecurityContextUtil.getAuthentication();
        if (authentication == null) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof NavPrincipal) {
            NavPrincipal thePrincipal = (NavPrincipal) principal;
            return ! thePrincipal.getUid().equals(EssoCookieUtil.ESSO_VALUE_ANONYMOUS);
        }

        return false;
    }


    public static int getSecurityLevel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return -1;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof NavPrincipal) {
            NavPrincipal thePrincipal = (NavPrincipal) principal;

            if (thePrincipal.getUid().equals(EssoCookieUtil.ESSO_VALUE_ANONYMOUS)) {
                return -1;
            } else {
                return Integer.parseInt(thePrincipal.getSecurityLevel());
            }
        }

        return -1;
    }
}
