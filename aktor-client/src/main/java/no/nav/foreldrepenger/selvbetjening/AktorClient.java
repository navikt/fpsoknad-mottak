package no.nav.foreldrepenger.selvbetjening;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.AktorId;
import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.Fodselsnummer;;

@Component
public class AktorClient  implements AktorOperations {
   
   private final String uri;
   
   public AktorClient(@Value("${AKTOER_V2_ENDPOINTURL:http://www.vg.no}") String uri) {
     this.uri = uri;
   }

   
   @Override
   public Optional<AktorId> aktorIdForFnr(Fodselsnummer fnr) {
        throw new UnsupportedOperationException();
    }
   
   @Override
   public String toString() {
      return getClass().getSimpleName() + " [uri=" + uri + "]";
   }
}
