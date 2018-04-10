package no.nav.foreldrepenger.oppslag.ws;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.policy.EndpointPolicy;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.attachment.reference.ReferenceResolver;
import org.apache.cxf.ws.policy.attachment.reference.RemoteReferenceResolver;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.neethi.Policy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class STSClientConfig {

	private static final String POLICY_PATH = "classpath:policy/";
   
    private  final String STS_REQUEST_SAML_POLICY = POLICY_PATH + "requestSamlPolicy.xml";
    private  final String STS_CLIENT_AUTHENTICATION_POLICY = POLICY_PATH + "untPolicy.xml";
    private  final String STS_REQUEST_SAML_POLICY_NO_TRANSPORTBINDING = POLICY_PATH + "requestSamlPolicyNoTransportBinding.xml";
    
    @Value("${stsclient.debug:false}")
    private boolean debug;
    
    private final URI stsUrl;
    private final String serviceUser;
    private final String servicePwd;

	public STSClientConfig(@Value("${SECURITYTOKENSERVICE_URL}") URI stsUrl, @Value("${FPSELVBETJENING_USERNAME}") String serviceUser, @Value("${FPSELVBETJENING_PASSWORD}") String servicePwd) {		
		this.stsUrl = stsUrl;
		this.serviceUser = serviceUser;
		this.servicePwd = servicePwd;
	}

	public <T> T configureRequestSamlToken(T port) {
        Client client = ClientProxy.getClient(port);
        // do not have onbehalfof token so cache token in endpoint
        configureStsRequestSamlToken(client, true);
        return port;
    }

    public <T> T configureRequestSamlTokenOnBehalfOfOidc(T port, OnBehalfOfOutInterceptor onBehalfOfOutInterceptor) {
        Client client = ClientProxy.getClient(port);
        client.getOutInterceptors().add(onBehalfOfOutInterceptor);
        // want to cache the token with the OnBehalfOfToken, not per proxy
        configureStsRequestSamlToken(client, false);
        return port;
    }

    protected void configureStsRequestSamlToken(Client client, boolean cacheTokenInEndpoint) {
        STSClient stsClient = new STSClient(client.getBus());
        String policy = STS_REQUEST_SAML_POLICY;
        if(debug){
            // For debugging
            stsClient.setFeatures(new ArrayList<Feature>(Arrays.asList(new LoggingFeature())));
            policy = STS_REQUEST_SAML_POLICY_NO_TRANSPORTBINDING;
        }
        configureStsWithPolicyForClient(stsClient, client, policy, cacheTokenInEndpoint);
    }

    protected void configureStsWithPolicyForClient(STSClient stsClient, Client client, String policyReference,
            boolean cacheTokenInEndpoint) {
        configureSTSClient(stsClient);
        client.getRequestContext().put(org.apache.cxf.rt.security.SecurityConstants.STS_CLIENT, stsClient);
        client.getRequestContext().put(org.apache.cxf.rt.security.SecurityConstants.CACHE_ISSUED_TOKEN_IN_ENDPOINT,
                cacheTokenInEndpoint);
        setEndpointPolicyReference(client, policyReference);
    }

    protected STSClient configureSTSClient(STSClient stsClient) {

        stsClient.setEnableAppliesTo(false);
        stsClient.setAllowRenewing(false);
        stsClient.setLocation(stsUrl.toString());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put(org.apache.cxf.rt.security.SecurityConstants.USERNAME, serviceUser);
        properties.put(org.apache.cxf.rt.security.SecurityConstants.PASSWORD, servicePwd);

        stsClient.setProperties(properties);
        // used for the STS client to authenticate itself to the STS provider.
        stsClient.setPolicy(STS_CLIENT_AUTHENTICATION_POLICY);
        return stsClient;
    }

    protected void setEndpointPolicyReference(Client client, String uri) {
        Policy policy = resolvePolicyReference(client, uri);
        setClientEndpointPolicy(client, policy);
    }
    
    private Policy resolvePolicyReference(Client client, String uri) {
        PolicyBuilder policyBuilder = client.getBus().getExtension(PolicyBuilder.class);
        ReferenceResolver resolver = new RemoteReferenceResolver("", policyBuilder);
        return resolver.resolveReference(uri);
    }

    private void setClientEndpointPolicy(Client client, Policy policy) {
        Endpoint endpoint = client.getEndpoint();
        EndpointInfo endpointInfo = endpoint.getEndpointInfo();

        PolicyEngine policyEngine = client.getBus().getExtension(PolicyEngine.class);
        SoapMessage message = new SoapMessage(Soap12.getInstance());
        EndpointPolicy endpointPolicy = policyEngine.getClientEndpointPolicy(endpointInfo, null, message);
        policyEngine.setClientEndpointPolicy(endpointInfo, endpointPolicy.updatePolicy(policy, message));
    }
}
