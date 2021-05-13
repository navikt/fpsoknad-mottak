package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.util.Constants.ISSUER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.core.jwt.JwtTokenClaims;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class TokenUtilTest {

    private static final Fødselsnummer FNR = new Fødselsnummer("42");
    @Mock
    private TokenValidationContextHolder holder;
    @Mock
    private TokenValidationContext context;
    @Mock
    private JwtTokenClaims claims;

    private TokenUtil tokenHelper;

    @BeforeEach
    void before() {
        when(holder.getTokenValidationContext()).thenReturn(context);
        when(context.getClaims(eq(ISSUER))).thenReturn(claims);
        tokenHelper = new TokenUtil(holder);
    }

    @Test
    void testOK() {
        when(claims.get(eq("exp")))
                .thenReturn(toDate(LocalDateTime.now().minusHours(1)).toInstant().getEpochSecond());
        when(claims.getSubject()).thenReturn(FNR.getFnr());
        assertEquals(FNR.getFnr(), tokenHelper.autentisertBruker());
        assertTrue(tokenHelper.erAutentisert());
    }

    @Test
    void testNoContext() {
        when(holder.getTokenValidationContext()).thenReturn(null);
        assertFalse(tokenHelper.erAutentisert());
        assertThrows(JwtTokenValidatorException.class, () -> tokenHelper.autentisertBruker());
    }

    @Test
    void testNoClaims() {
        when(context.getClaims(eq("selvbetjening"))).thenReturn(null);
        assertFalse(tokenHelper.erAutentisert());
        assertThrows(JwtTokenValidatorException.class, () -> tokenHelper.autentisertBruker());
    }

    @Test
    void testNoClaimset() {
        when(context.getClaims(eq(ISSUER))).thenReturn(null);
        assertFalse(tokenHelper.erAutentisert());
        assertThrows(JwtTokenValidatorException.class, () -> tokenHelper.autentisertBruker());
    }

    @Test
    void testNoSubject() {
        when(claims.getSubject()).thenReturn(null);
        assertFalse(tokenHelper.erAutentisert());
        assertThrows(JwtTokenValidatorException.class, () -> tokenHelper.autentisertBruker());
    }

    private static Date toDate(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

}
