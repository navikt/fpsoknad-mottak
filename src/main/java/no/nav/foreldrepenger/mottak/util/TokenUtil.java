package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.common.util.Constants.TOKENX;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.util.DateUtils;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.felles.Kjønn;
import no.nav.foreldrepenger.common.util.TimeUtil;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.core.jwt.JwtTokenClaims;

@Component
public class TokenUtil {

    public static final String BEARER = "Bearer ";

    private final TokenValidationContextHolder ctxHolder;

    public TokenUtil(TokenValidationContextHolder ctxHolder) {
        this.ctxHolder = ctxHolder;
    }

    public boolean erUtløpt() {
        return Optional.ofNullable(getExpiration())
                .filter(d -> d.isBefore(LocalDateTime.now()))
                .isPresent();
    }

    public Kjønn kjønn() {
        return fnr().kjønn();
    }

    public boolean erAutentisert() {
        return getSubject() != null;
    }

    public LocalDateTime getExpiration() {
        return Optional.ofNullable(claimSet())
            .map(c -> c.get("exp"))
            .map(this::getDateClaim)
            .map(TimeUtil::fraDato)
            .orElse(null);
    }

    public Fødselsnummer fnr() {
        return new Fødselsnummer(autentisertBruker());
    }

    private String getSubject() {
        return Optional.ofNullable(claimSet())
            .map(this::getSubjectFromPidOrSub)
            .orElse(null);
    }

    private String getSubjectFromPidOrSub(JwtTokenClaims claims) {
        return Optional.ofNullable(claims.getStringClaim("pid"))
            .orElseGet(claims::getSubject);
    }

    private String autentisertBruker() {
        return Optional.ofNullable(getSubject())
            .orElseThrow(unauthenticated("Fant ikke subject, antagelig ikke autentisert"));
    }

    private static Supplier<? extends JwtTokenValidatorException> unauthenticated(String msg) {
        return () -> new JwtTokenValidatorException(msg);
    }

    private JwtTokenClaims claimSet() {
        return Optional.ofNullable(context())
            .map(s -> s.getClaims(TOKENX))
            .orElse(null);
    }

    private TokenValidationContext context() {
        return ctxHolder.getTokenValidationContext();
    }

    private Date getDateClaim(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date d) {
            return d;
        }
        if (value instanceof Number n) {
            return DateUtils.fromSecondsSinceEpoch(n.longValue());
        }
        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ctxHolder=" + ctxHolder + "]";
    }

}
