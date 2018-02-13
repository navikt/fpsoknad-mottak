package no.nav.foreldrepenger.mottak.pdf;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class PdfGeneratorTest {

   @Test
   public void smokeTest() throws Exception {
      SoeknadsskjemaEngangsstoenad soknad = new SoeknadsskjemaEngangsstoenad();
      Bruker bruker = new Bruker();
      bruker.setPersonidentifikator("12345678910");
      soknad.setBruker(bruker);
      Soknadsvalg valg = new Soknadsvalg();
      valg.setStoenadstype(Stoenadstype.ENGANGSSTOENADMOR);
      soknad.setSoknadsvalg(valg);
      TilknytningNorge tilknytningNorge = new TilknytningNorge();
      soknad.setTilknytningNorge(tilknytningNorge);

      PdfGenerator generator = new PdfGenerator();
      byte[] pdf = generator.generate(soknad);
      assertEquals(5655, pdf.length);
      assertTrue(hasPdfSignature(pdf));
   }

   private boolean hasPdfSignature(byte[] bytes) {
      return bytes[0] == 0x25 &&
         bytes[1] == 0x50 &&
         bytes[2] == 0x44 &&
         bytes[3] == 0x46;
   }

}
