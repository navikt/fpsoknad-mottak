package no.nav.foreldrepenger.lookup.rest.sak;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StsClientTest {

    @Test
    public void ableToReadTemplateAtInstantiation() {
        new StsClient(null, "http//whatever", "myuser", "mypw");
    }

    @Test
    public void placeholdersInTemplateAreReplaced() {
        final StsClient stsClient = new StsClient(null, "http://whatever", "myuser", "mypw");
        final String processed = stsClient.replacePlaceholders("MY.OIDC.TOKEN");
        assertTrue(processed.startsWith("<soap"));
        assertTrue(processed.contains("<wsse:Username>myuser</wsse:Username>"));
        assertTrue(processed.contains("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">mypw</wsse:Password>"));
        assertTrue(processed.contains("MY.OIDC.TOKEN"));
    }

}
