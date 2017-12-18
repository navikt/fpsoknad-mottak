package no.nav.foreldrepenger.selvbetjening.cxfclient;


import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.policy.EndpointPolicy;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.attachment.reference.RemoteReferenceResolver;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.neethi.Policy;

/**
 * A collection of configuration methods to configure an CXF WS-client
 * to use STS to retrieve SAML tokens for end user and system user.
 */
 public class STSConfigurationUtility {
  

    /**
     * Configures endpoint to get SAML token for the end user from STS in exchange for OpenAM token.
     * The SAML token will be added as a SupportingToken to the WS-Security headers.
     * <p/>
     * 1. Binds a WS-SecurityPolicy to the endpoint/client.
     * The policy requires a SupportingToken of type IssuedToken.
     * <p/>
     * 2. Configures the location and credentials of the STS.
     *
     * @param client CXF client
     */
    public static void configureStsForExternalSSO(Client client, String location, STSConfig stsConfig) {
        STSClient stsClient = createBasicSTSClient(client.getBus(), location, stsConfig);
        stsClient.setClaimsCallbackHandler(new STSClaimsCallbackHandler());

        client.getRequestContext().put("ws-security.sts.client", stsClient);
        client.getRequestContext().put(SecurityConstants.CACHE_ISSUED_TOKEN_IN_ENDPOINT, false);
        setEndpointPolicyReference(client, "classpath:policies/stspolicy.xml");
    }

    /**
     * Configures endpoint to get SAML token for the system user from STS.
     * The SAML token will be added as a SupportingToken to the WS-Security headers.
     * <p/>
     * 1. Binds a WS-SecurityPolicy to the endpoint/client.
     * The policy requires a SupportingToken of type IssuedToken.
     * <p/>
     * 2. Configures the location and credentials of the STS.
     *
     * @param client CXF client
     */
    public static void configureStsForSystemUser(Client client, String location, STSConfig stsConfig) {
        new WSAddressingFeature().initialize(client, client.getBus());
        STSClient stsClient = createBasicSTSClient(client.getBus(), location, stsConfig);
        client.getRequestContext().put("ws-security.sts.client", stsClient);
        setEndpointPolicyReference(client, "classpath:policies/stspolicy.xml");
    }
    
    public static void configureStsForOnBehalfOfWithUNT(Client client, String location, STSConfig stsConfig) {
        STSClient stsClient = createBasicSTSClient(client.getBus(), location, stsConfig);
        stsClient.setOnBehalfOf(new STSClaimsCallbackHandler());
        client.getRequestContext().put("ws-security.sts.client", stsClient);
        client.getRequestContext().put(SecurityConstants.CACHE_ISSUED_TOKEN_IN_ENDPOINT, false);
        setEndpointPolicyReference(client, "classpath:policies/stspolicy.xml");
    }

    private static STSClient createBasicSTSClient(Bus bus, String location, STSConfig stsConfig) {
        STSClient stsClient = new NAVSTSClient(bus);
        stsClient.setWsdlLocation("wsdl/ws-trust-1.4-service.wsdl");
        stsClient.setServiceQName(new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512/wsdl", "SecurityTokenServiceProvider"));
        stsClient.setEndpointQName(new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512/wsdl", "SecurityTokenServiceSOAP"));
        stsClient.setEnableAppliesTo(false);
        stsClient.setAllowRenewing(false);

        try {
            // Endpoint must be set on clients request context
            // as the wrapping requestcontext is not available
            // when creating the client from WSDL (ref cxf-users mailinglist)
            stsClient.getClient().getRequestContext().put(Message.ENDPOINT_ADDRESS, location);
        } catch (BusException | EndpointException e) {
            throw new RuntimeException("Failed to set endpoint adress of STSClient", e);
        }

        stsClient.getOutInterceptors().add(new LoggingOutInterceptor());
        stsClient.getInInterceptors().add(new LoggingInInterceptor());


        Map<String, Object> properties = new HashMap<>();
        properties.put(SecurityConstants.USERNAME, stsConfig.getSystemUserName());
        properties.put(SecurityConstants.PASSWORD, stsConfig.getSystemUserPassword());
        stsClient.setProperties(properties);
        return stsClient;
    }

    private static void setEndpointPolicyReference(Client client, String uri) {
        setClientEndpointPolicy(client, resolvePolicyReference(client, uri));
    }

    private static Policy resolvePolicyReference(Client client, String uri) {
        return new RemoteReferenceResolver("", client.getBus().getExtension(PolicyBuilder.class)).resolveReference(uri);
    }

    private static void setClientEndpointPolicy(Client client, Policy policy) {
        EndpointInfo endpointInfo = client.getEndpoint().getEndpointInfo();
        PolicyEngine policyEngine = client.getBus().getExtension(PolicyEngine.class);
        EndpointPolicy endpointPolicy = policyEngine.getClientEndpointPolicy(endpointInfo, client.getConduit(), null);
        policyEngine.setClientEndpointPolicy(endpointInfo, endpointPolicy.updatePolicy(policy, null));
    }
}