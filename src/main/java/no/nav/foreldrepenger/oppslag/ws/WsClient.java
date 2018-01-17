package no.nav.foreldrepenger.oppslag.ws;

import java.util.Arrays;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptor;

public class WsClient<T> {

	public T createPort(String serviceUrl, Class<?> portType, PhaseInterceptor<? extends Message>... interceptors) {
		JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
		jaxWsProxyFactoryBean.setServiceClass(portType);
		jaxWsProxyFactoryBean.setAddress(serviceUrl);
		T port = (T) jaxWsProxyFactoryBean.create();
		Client client = ClientProxy.getClient(port);
		Arrays.stream(interceptors).forEach(client.getOutInterceptors()::add);
		STSClientConfig.configureRequestSamlToken(port);
		return port;
	}

}
