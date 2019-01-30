package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.Constants.ISSUER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.nimbusds.jwt.JWTClaimsSet;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.oidc.exceptions.OIDCTokenValidatorException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
public class TokenUtilTest {

    private static final Fødselsnummer FNR = new Fødselsnummer("42");
    @Mock
    private OIDCRequestContextHolder holder;
    @Mock
    private OIDCValidationContext context;
    @Mock
    private OIDCClaims claims;

    private TokenUtil tokenHelper;

    @BeforeEach
    public void before() {
        when(holder.getOIDCValidationContext()).thenReturn(context);
        when(context.getClaims(eq(ISSUER))).thenReturn(claims);
        tokenHelper = new TokenUtil(holder);
    }

    @Test
    public void testExtractorOK() {
        when(claims.getClaimSet()).thenReturn(new JWTClaimsSet.Builder().subject(FNR.getFnr()).build());
        assertEquals(FNR.getFnr(), tokenHelper.autentisertBruker());
        assertTrue(tokenHelper.erAutentisert());
    }

    @Test
    public void testExtractorNoContext() {
        when(holder.getOIDCValidationContext()).thenReturn(null);
        assertFalse(tokenHelper.erAutentisert());
        assertThrows(OIDCTokenValidatorException.class, () -> tokenHelper.autentisertBruker());
    }

    @Test
    public void testExtractorNoClaims() {
        when(context.getClaims(eq("selvbetjening"))).thenReturn(null);
        assertFalse(tokenHelper.erAutentisert());
        assertThrows(OIDCTokenValidatorException.class, () -> tokenHelper.autentisertBruker());
    }

    @Test
    public void testExtractorNoClaimset() {
        when(claims.getClaimSet()).thenReturn(null);
        assertFalse(tokenHelper.erAutentisert());
        assertThrows(OIDCTokenValidatorException.class, () -> tokenHelper.autentisertBruker());
    }

    @Test
    public void testExtractorNoSubject() {
        when(claims.getClaimSet()).thenReturn(new JWTClaimsSet.Builder().build());
        assertFalse(tokenHelper.erAutentisert());
        assertThrows(OIDCTokenValidatorException.class, () -> tokenHelper.autentisertBruker());
    }
}
