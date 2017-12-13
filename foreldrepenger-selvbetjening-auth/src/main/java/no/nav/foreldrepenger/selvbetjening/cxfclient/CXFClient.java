package no.nav.foreldrepenger.selvbetjening.cxfclient;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import no.nav.foreldrepenger.selvbetjening.cxfclient.features.TimeoutFeature;
import no.nav.foreldrepenger.selvbetjening.cxfclient.interceptors.LoggingFeatureUtenBinaryOgUtenSamlTokenLogging;

/**
 * CXFClient som benytter JaxWsProxyFactoryBean for å lage en proxy for å kunne legge til ulike features.
 */
public class CXFClient<T> {

  public final JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
  private final Class<T> serviceClass;
  private STSConfig stsConfig;
  private final List<Handler> handlerChain = new ArrayList<>();
  private boolean configureStsForExternalSSO;
  private boolean configureStsForSystemUser;
  private int connectionTimeout = TimeoutFeature.DEFAULT_CONNECTION_TIMEOUT;
  private int receiveTimeout = TimeoutFeature.DEFAULT_RECEIVE_TIMEOUT;

  public CXFClient(Class<T> serviceClass) {
    factoryBean.getFeatures().add(new LoggingFeatureUtenBinaryOgUtenSamlTokenLogging());
    factoryBean.getFeatures().add(new WSAddressingFeature());
    factoryBean.setProperties(new HashMap<>());
    this.serviceClass = serviceClass;
  }

  private static void disableCNCheckIfConfigured(Client client) {
    HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
    httpConduit.setTlsClientParameters(new TLSClientParameters());
    if (Boolean.valueOf(System.getProperty("disable.ssl.cn.check", "false"))) {
      httpConduit.getTlsClientParameters().setDisableCNCheck(true);
    }
  }

  public CXFClient<T> serviceUrl(String url) {
    factoryBean.setAddress(url);
    return this;
  }

  public CXFClient<T> wsdl(String url) {
    factoryBean.setWsdlURL(url);
    return this;
  }

  public CXFClient<T> configureStsForExternalSSO() {
    configureStsForExternalSSO = true;
    return this;
  }

  public CXFClient<T> configureStsForSystemUser(STSConfig stsConfig) {
    configureStsForSystemUser = true;
    return this;
  }

  public CXFClient<T> withProperty(String key, Object value) {
    factoryBean.getProperties().put(key, value);
    return this;
  }

  public CXFClient<T> timeout(int connectionTimeout, int receiveTimeout) {
    this.connectionTimeout = connectionTimeout;
    this.receiveTimeout = receiveTimeout;
    return this;
  }

  public CXFClient<T> enableMtom() {
    factoryBean.getProperties().put("mtom-enabled", true);
    return this;
  }

  public CXFClient<T> withHandler(Handler handler, Handler... moreHandlers) {
    handlerChain.add(handler);
    handlerChain.addAll(asList(moreHandlers));
    return this;
  }

  public CXFClient<T> serviceName(QName serviceName) {
    factoryBean.setServiceName(serviceName);
    return this;
  }

  public CXFClient<T> endpointName(QName endpointName) {
    factoryBean.setEndpointName(endpointName);
    return this;
  }

  @SafeVarargs
  public final CXFClient<T> withOutInterceptor(Interceptor<? extends Message> interceptor,
                                               Interceptor<? extends Message>... moreInterceptors) {
    List<Interceptor<? extends Message>> outInterceptors = factoryBean.getOutInterceptors();
    outInterceptors.add(interceptor);
    outInterceptors.addAll(asList(moreInterceptors));
    return this;
  }

  public T build() {
    factoryBean.getFeatures().add(new TimeoutFeature(receiveTimeout, connectionTimeout));
    T portType = factoryBean.create(serviceClass);
    Client client = ClientProxy.getClient(portType);
    disableCNCheckIfConfigured(client);

    if (configureStsForExternalSSO) {
      STSConfigurationUtility.configureStsForExternalSSO(client, factoryBean.getAddress(),stsConfig);
    }
    if (configureStsForSystemUser) {
      STSConfigurationUtility.configureStsForSystemUser(client, factoryBean.getAddress(),stsConfig);
    }

    ((BindingProvider) portType).getBinding().setHandlerChain(handlerChain);
    return portType;
  }

}
