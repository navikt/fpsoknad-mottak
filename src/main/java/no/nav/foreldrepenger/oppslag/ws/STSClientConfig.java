package no.nav.foreldrepenger.oppslag.ws;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.ws.security.trust.STSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class STSClientConfig {

	private static final String POLICY_PATH = "classpath:policy/";
    private static final String STS_CLIENT_AUTHENTICATION_POLICY = POLICY_PATH + "untPolicy.xml";
     
    @Value("${SECURITYTOKENSERVICE_URL}")
    private URI stsUrl;
    
    @Value("${FPSELVBETJENING_USERNAME}")
    private String serviceUser;
    
    @Value("${FPSELVBETJENING_PASSWORD}")
    private String servicePwd;

	@Bean
    public STSClient configureSTSClient(Bus bus, @Value("${stsclientconfig.debug:false}") boolean debug) {
    	STSClient stsClient = new STSClient(bus);
        if(debug){
           stsClient.setFeatures(new ArrayList<Feature>(Arrays.asList(new LoggingFeature())));     
        } 	
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
}
