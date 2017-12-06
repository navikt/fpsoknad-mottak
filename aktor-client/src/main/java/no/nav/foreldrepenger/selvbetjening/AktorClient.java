package no.nav.foreldrepenger.selvbetjening;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;

public class AktorClient  implements AktorOperations {
   
   private final WebServiceTemplate template;
   
   
   public AktorClient(@Value("${integration.aktoridservice}") String uri) {
      this(templateFrom(uri));
   }

    private AktorClient(WebServiceTemplate template) {
      this.template = Objects.requireNonNull(template);
   }

  
   private static WebServiceTemplate templateFrom(String uri) {
      WebServiceTemplate template = new WebServiceTemplate();
      template.setDefaultUri(uri);
      return template;
   }

   
   @Override
   public String aktorIdForFnr(String fnr) {
        HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
        request.setIdent(fnr);
        ClientInterceptor[] interceptors = new ClientInterceptor[1];
        Wss4jSecurityInterceptor interceptor = new Wss4jSecurityInterceptor();
        //interceptor.setSecurement
        //interceptors[0] = interceptor;
        template.setInterceptors(interceptors);
        HentAktoerIdForIdentResponse response = (HentAktoerIdForIdentResponse) template.marshalSendAndReceive(request);
        return response.getHentAktoerIdForIdentResponse().getAktoerId();
    }
   
   @Override
   public String toString() {
      return getClass().getSimpleName() + " [template=" + template + "]";
   }
}
