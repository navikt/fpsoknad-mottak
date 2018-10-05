package no.nav.foreldrepenger.mottak.util;

import static no.nav.security.oidc.OIDCConstants.OIDC_VALIDATION_CONTEXT;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.nimbusds.jwt.JWTClaimsSet;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.http.errorhandling.ForbiddenException;
import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FnrExtractorTest {

    @Mock
    private OIDCRequestContextHolder holder;
    @Mock
    private OIDCValidationContext context;
    @Mock
    private OIDCClaims claims;

    @Test
    public void testExtractorOK() {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject("42").build();
        when(claims.getClaimSet()).thenReturn(claimsSet);
        when(context.getClaims(eq("selvbetjening"))).thenReturn(claims);
        when(holder.getRequestAttribute(eq(OIDC_VALIDATION_CONTEXT))).thenReturn(context);
        FnrExtractor extractor = new FnrExtractor(holder);
        assertEquals(new Fødselsnummer("42"), extractor.fnrFromToken());
    }

    @Test(expected = ForbiddenException.class)
    public void testExtractorNoToken() {
        when(holder.getRequestAttribute(eq(OIDC_VALIDATION_CONTEXT))).thenReturn(null);
        FnrExtractor extractor = new FnrExtractor(holder);
        assertEquals(extractor.hasToken(), false);
        extractor.fnrFromToken();
    }

    @Test(expected = ForbiddenException.class)
    public void testExtractorNoClaims() {
        when(holder.getRequestAttribute(eq(OIDC_VALIDATION_CONTEXT))).thenReturn(context);
        when(context.getClaims(eq("selvbetjening"))).thenReturn(null);
        FnrExtractor extractor = new FnrExtractor(holder);
        assertEquals(extractor.hasToken(), false);
        extractor.fnrFromToken();
    }

    @Test(expected = ForbiddenException.class)
    public void testExtractorNoClaimset() {
        when(holder.getRequestAttribute(eq(OIDC_VALIDATION_CONTEXT))).thenReturn(context);
        when(context.getClaims(eq("selvbetjening"))).thenReturn(claims);
        when(claims.getClaimSet()).thenReturn(null);
        FnrExtractor extractor = new FnrExtractor(holder);
        assertEquals(extractor.hasToken(), false);
        extractor.fnrFromToken();
    }

    @Test(expected = ForbiddenException.class)
    public void testExtractorNoSubject() {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().build();
        when(holder.getRequestAttribute(eq(OIDC_VALIDATION_CONTEXT))).thenReturn(context);
        when(context.getClaims(eq("selvbetjening"))).thenReturn(claims);
        when(claims.getClaimSet()).thenReturn(claimsSet);
        FnrExtractor extractor = new FnrExtractor(holder);
        assertEquals(extractor.hasToken(), false);
        extractor.fnrFromToken();
    }
}
