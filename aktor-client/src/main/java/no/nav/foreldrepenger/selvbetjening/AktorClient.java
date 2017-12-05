package no.nav.foreldrepenger.selvbetjening;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.client.core.WebServiceTemplate;

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
        HentAktoerIdForIdentResponse response = (HentAktoerIdForIdentResponse) template.marshalSendAndReceive(request);
        return response.getHentAktoerIdForIdentResponse().getAktoerId();
    }
   
   @Override
   public String toString() {
      return getClass().getSimpleName() + " [template=" + template + "]";
   }
}
