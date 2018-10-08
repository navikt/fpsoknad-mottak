package no.nav.foreldrepenger.lookup.ws;

import static no.nav.foreldrepenger.lookup.EnvUtil.CONFIDENTIAL;

import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.rt.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.oidc.context.TokenContext;
import no.nav.security.spring.oidc.SpringOIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.interceptor.OIDCUnauthorizedException;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OnBehalfOfOutInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = LoggerFactory.getLogger(OnBehalfOfOutInterceptor.class);

    @Inject
    private SpringOIDCRequestContextHolder oidcRequestContextHolder;

    public enum TokenType {
        OIDC("urn:ietf:params:oauth:token-type:jwt");
        public String valueType;

        TokenType(String valueType) {
            this.valueType = valueType;
        }
    }

    public OnBehalfOfOutInterceptor() {
        super(Phase.SETUP);
    }

    @Override
    public void handleMessage(Message message) throws Fault {

        LOG.debug("looking up OnBehalfOfToken from SpringOIDCRequestContextHolder.");
        String token = getTokenFromFirstIssuerInValidationContext();

        if (token != null) {
            byte[] tokenBytes = token.getBytes();
            String wrappedToken = wrapTokenForTransport(tokenBytes, TokenType.OIDC);
            message.put(SecurityConstants.STS_TOKEN_ON_BEHALF_OF, createOnBehalfOfElement(wrappedToken));
        }
        else {
            LOG.warn("could not find OnBehalfOfToken token in requestcontext.");
            throw new OIDCUnauthorizedException("no OIDC token found when attempting to invoke sts.");
        }
    }

    private String getTokenFromFirstIssuerInValidationContext() {
        OIDCValidationContext context = oidcRequestContextHolder.getOIDCValidationContext();
        List<String> issuers = context.getIssuers();
        if (context != null && issuers != null) {
            String issuerName = issuers.stream().filter(Objects::nonNull).findFirst().orElse(null);
            LOG.debug("getting first issuer in validation context: " + issuerName);
            TokenContext token = context.getToken(issuerName);
            LOG.debug(CONFIDENTIAL, "found token: " + token);
            return token != null ? token.getIdToken() : null;
        }
        LOG.warn("no issuers found in oidcvalidationcontext. returning null");
        return null;
    }

    private String wrapTokenForTransport(byte[] token, TokenType tokenType) {
        switch (tokenType) {
        case OIDC:
            return wrapWithBinarySecurityToken(token, tokenType.valueType);
        default:
            throw new RuntimeException("unsupported token type:" + tokenType);
        }
    }

    private static Element createOnBehalfOfElement(String content) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(content)));
            return document.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String wrapWithBinarySecurityToken(byte[] token, String valueType) {
        String base64encodedToken = Base64.getEncoder().encodeToString(token);
        return "<wsse:BinarySecurityToken xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\""
                + " EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\""
                + " ValueType=\"" + valueType + "\" >" + base64encodedToken + "</wsse:BinarySecurityToken>";
    }
}
