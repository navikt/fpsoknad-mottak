package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.common.util.Constants.ISSUER;
import static no.nav.foreldrepenger.common.util.Constants.TOKENX;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.util.DateUtils;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.felles.Kjønn;
import no.nav.foreldrepenger.common.util.TimeUtil;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.core.jwt.JwtToken;
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

    public String bearerToken() {
        return erAutentisert() ? BEARER + getToken() : null;
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

    public AuthenticationLevel getLevel() {
        return Optional.ofNullable(claimSet())
                .map(c -> c.get("acr"))
                .map(String.class::cast)
                .map(AuthenticationLevel::of)
                .orElse(AuthenticationLevel.NONE);
    }

    public String getSubject() {
        return Optional.ofNullable(claimSet())
            .map(this::getSubjectFromPidOrSub)
            .orElse(null);
    }

    private String getSubjectFromPidOrSub(JwtTokenClaims claims) {
        return Optional.ofNullable(claims.getStringClaim("pid"))
            .orElseGet(claims::getSubject);
    }

    public boolean harTokenFor(String issuer) {
        return Optional.ofNullable(context())
            .map(s -> s.hasTokenFor(issuer))
            .orElse(false);
    }

    public String autentisertBruker() {
        return Optional.ofNullable(getSubject())
                .orElseThrow(unauthenticated("Fant ikke subject, antagelig ikke autentisert"));
    }

    public Fødselsnummer fnr() {
        return new Fødselsnummer(autentisertBruker());
    }

    private static Supplier<? extends JwtTokenValidatorException> unauthenticated(String msg) {
        return () -> new JwtTokenValidatorException(msg);
    }

    private JwtTokenClaims claimSet() {
        return Stream.of(ISSUER, TOKENX)
                .map(this::claimSet)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private JwtTokenClaims claimSet(String issuer) {
        return Optional.ofNullable(context())
                .map(s -> s.getClaims(issuer))
                .orElse(null);
    }

    private TokenValidationContext context() {
        return Optional.ofNullable(ctxHolder.getTokenValidationContext())
                .orElse(null);
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

    public String getToken() {
        return Stream.of(ISSUER, TOKENX)
                .map(this::getToken)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(unauthenticated("Fant ikke ID-token"));

    }

    private String getToken(String issuer) {
        return Optional.ofNullable(context())
                .map(c -> c.getJwtToken(issuer))
                .filter(Objects::nonNull)
                .map(JwtToken::getTokenAsString)
                .orElse(null);
    }

    public JwtToken getJWTToken(String issuer) {
        return ctxHolder.getTokenValidationContext().getJwtToken(issuer);

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ctxHolder=" + ctxHolder + "]";
    }

}
