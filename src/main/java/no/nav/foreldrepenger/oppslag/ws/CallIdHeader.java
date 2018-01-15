package no.nav.foreldrepenger.oppslag.ws;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class CallIdHeader extends AbstractPhaseInterceptor<Message> {

   public CallIdHeader() {
      super(Phase.PRE_STREAM);
   }

   @Override
   public void handleMessage(Message message) throws Fault {
      try {
         QName qName = new QName("uri:no.nav.applikasjonsrammeverk", "callId");
         SoapHeader header = new SoapHeader(qName,
            randomValue(), new JAXBDataBinding(String.class));
         ((SoapMessage) message).getHeaders().add(header);
      } catch (JAXBException ex) {
         ex.printStackTrace();
      }
   }

   private String randomValue() {
      return "fpoppslag-" + (int) (Math.random() * 10000);
   }

}
