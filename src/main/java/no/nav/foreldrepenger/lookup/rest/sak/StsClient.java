package no.nav.foreldrepenger.lookup.rest.sak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

import static java.util.stream.Collectors.joining;

public class StsClient {

    private static final Logger log = LoggerFactory.getLogger(StsClient.class);

    private RestTemplate restTemplate;
    private String stsUrl;
    private String serviceUser;
    private String servicePwd;
    private String soapRequestTemplate;

    public StsClient(RestTemplate restTemplate, String stsUrl, String serviceUser, String servicePwd) {
        this.restTemplate = restTemplate;
        this.stsUrl = stsUrl;
        this.serviceUser = serviceUser;
        this.servicePwd = servicePwd;
        soapRequestTemplate = readTemplate();
    }

    public String exchangeForSamlToken(String oidcToken) {
        log.info("Attempting OIDC to SAML token exchange from " + stsUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "text/xml");
        headers.set("SOAPAction", "http://docs.oasis-open.org/ws-sx/ws-trust/200512/RST/Issue");
        String requestBody = replacePlaceholders(Base64.getEncoder().encodeToString(oidcToken.getBytes()));
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(stsUrl, HttpMethod.POST, requestEntity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error while exchanging token, STS returned " + response.getStatusCode());
        }
        log.info("Got SAMl token");
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

}
