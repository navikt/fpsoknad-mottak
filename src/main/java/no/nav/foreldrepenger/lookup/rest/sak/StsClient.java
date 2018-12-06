package no.nav.foreldrepenger.lookup.rest.sak;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

public class StsClient {

    private static final Logger LOG = LoggerFactory.getLogger(StsClient.class);

    private final RestOperations restOperations;
    private final String stsUrl;
    private final String serviceUser;
    private final String servicePwd;
    private final String soapRequestTemplate;

    public StsClient(RestOperations restOperations, String stsUrl, String serviceUser, String servicePwd) {
        this.restOperations = restOperations;
        this.stsUrl = stsUrl;
        this.serviceUser = serviceUser;
        this.servicePwd = servicePwd;
        this.soapRequestTemplate = readTemplate();
    }

    public String exchangeForSamlToken(String oidcToken) {
        LOG.trace("Attempting OIDC to SAML token exchange from {}", stsUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, TEXT_XML_VALUE);
        headers.set("SOAPAction", "http://docs.oasis-open.org/ws-sx/ws-trust/200512/RST/Issue");
        String requestBody = replacePlaceholders(Base64.getEncoder().encodeToString(oidcToken.getBytes()));
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restOperations.exchange(stsUrl, HttpMethod.POST, requestEntity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error while exchanging token, STS returned " + response.getStatusCode());
        }
        LOG.trace("Got SAML token");
        return extractSamlAssertionFrom(response.getBody());
    }

    private String readTemplate() {
        try (InputStream stream = StsClient.class.getResourceAsStream("/template/stsenvelope.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines().collect(joining("\n"));
        } catch (Exception ex) {
            throw new RuntimeException("Error while reading SOAP request template", ex);
        }
    }

    protected String replacePlaceholders(String oidcToken) {
        return soapRequestTemplate.replace("%SOMESERVICEUSER%", serviceUser)
                .replace("%THEPASSWORD%", servicePwd)
                .replace("%OIDCTOKEN%", oidcToken);
    }

    protected String extractSamlAssertionFrom(String envelope) {
        int startIdx = envelope.indexOf("<saml2:Assertion");
        int endIdx = envelope.indexOf("</saml2:Assertion>") + 18;
        return envelope.substring(startIdx, endIdx);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [restOperations=" + restOperations + ", stsUrl=" + stsUrl
                + ", serviceUser=" + serviceUser
                + ", servicePwd=" + servicePwd + ", soapRequestTemplate=" + soapRequestTemplate + "]";
    }

}
