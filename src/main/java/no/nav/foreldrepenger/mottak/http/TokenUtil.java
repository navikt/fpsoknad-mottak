package no.nav.foreldrepenger.mottak.http;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.nimbusds.jwt.util.DateUtils;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.util.TimeUtil;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.core.jwt.JwtTokenClaims;

@Configuration
public class TokenUtil {
    public static final String TOKENX = "tokenx";
    public static final String BEARER = "Bearer ";
    public static final String ACR_IDPORTEN_LEGACY = "acr=Level4";
    public static final String ACR_IDPORTEN = "acr=idporten-loa-high";
    private static final List<String> issuers = List.of(TOKENX);

    private final TokenValidationContextHolder ctxHolder;

    @Autowired
    public TokenUtil(TokenValidationContextHolder ctxHolder) {
        this.ctxHolder = ctxHolder;
    }

    public Fødselsnummer autentisertBrukerOrElseThrowException() {
        return new Fødselsnummer(fødselsnummerFraToken());
    }

    public boolean erInnloggetBruker() {
        return getSubject() != null;
    }

    public boolean erUtløpt() {
        return Optional.ofNullable(getExpiration())
            .filter(d -> d.isBefore(LocalDateTime.now()))
            .isPresent();
    }

    public LocalDateTime getExpiration() {
        return Optional.ofNullable(claimSet())
            .map(c -> c.get("exp"))
            .map(TokenUtil::getDateClaim)
            .map(TimeUtil::fraDato)
            .orElse(null);
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

    private String fødselsnummerFraToken() {
        return Optional.ofNullable(getSubject())
            .orElseThrow(() -> new JwtTokenValidatorException("Fant ikke subject, antagelig ikke autentisert"));
    }

    private JwtTokenClaims claimSet() {
        return issuers.stream()
                .map(this::claimSet)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private JwtTokenClaims claimSet(String issuer) {
        return Optional.ofNullable(context())
                .map(s -> claimsFraKontekts(issuer, s))
                .orElse(null);
    }

    private static JwtTokenClaims claimsFraKontekts(String issuer, TokenValidationContext context) {
        try {
            return context.getClaims(issuer);
        } catch (Exception e) {
            return null;
        }
    }

    private TokenValidationContext context() {
        return ctxHolder.getTokenValidationContext();
    }

    private static Date getDateClaim(Object value) {
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
}
