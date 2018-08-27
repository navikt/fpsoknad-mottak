package no.nav.foreldrepenger.lookup.ws;

import java.util.Objects;

import javax.inject.Inject;

import no.nav.foreldrepenger.lookup.UUIDCallIdGenerator;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class WsClient<T> {

    @Inject
    private EndpointSTSClientConfig endpointStsClientConfig;

    @Inject
    UUIDCallIdGenerator generator;
    @Inject
    private OnBehalfOfOutInterceptor onBehalfOfOutInterceptor;
    
    public T createPort(String serviceUrl, Class<?> portType) {
        T port  = createAndConfigurePort(serviceUrl, portType);
        endpointStsClientConfig.configureRequestSamlTokenOnBehalfOfOidc(port, onBehalfOfOutInterceptor);
        return port;
    }
    
    public T createPortForHealthIndicator(String serviceUrl, Class<?> portType) {
        T port  = createAndConfigurePort(serviceUrl, portType);
        endpointStsClientConfig.configureRequestSamlToken(port);
        return port;
    }
    
    @SuppressWarnings("unchecked")
    private T createAndConfigurePort(String serviceUrl, Class<?> portType){
    	JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(portType);
        jaxWsProxyFactoryBean.setAddress(Objects.requireNonNull(serviceUrl));
        T port = (T) jaxWsProxyFactoryBean.create();
        Client client = ClientProxy.getClient(port);
        client.getOutInterceptors().add(new CallIdHeader(generator));
        return port;
    }
}
