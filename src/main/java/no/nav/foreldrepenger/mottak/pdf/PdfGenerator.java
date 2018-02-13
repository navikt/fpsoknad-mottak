package no.nav.foreldrepenger.mottak.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static no.nav.foreldrepenger.mottak.pdf.SøknadsinfoExtractor.*;

public class PdfGenerator {

   private static final Font headingFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD);
   private static final Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);

   private enum LINE_TYPE {HEADING, NORMAL}

   public byte[] generate(SoeknadsskjemaEngangsstoenad soknad) throws Exception {

      Document document = new Document();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PdfWriter.getInstance(document, baos);

      document.open();

      Path path = Paths.get(ClassLoader.getSystemResource("pdf/nav-logo.png").toURI());
      Image logo = Image.getInstance(path.toAbsolutePath().toString());
      logo.setAlignment(Image.ALIGN_CENTER);
      document.add(logo);

      document.add(centeredParagraph("Søknad om engangsstønad", LINE_TYPE.HEADING));
      document.add(centeredParagraph(userId(soknad), LINE_TYPE.NORMAL));
      document.add(separator());

      document.add(blankLine());

      document.add(paragraph("Informasjon om barnet", LINE_TYPE.HEADING));
      document.add(paragraph("Søknaden gjelder " + childCount(soknad) + " barn", LINE_TYPE.NORMAL));
      document.add(paragraph("Med termindato den " + termindato(soknad), LINE_TYPE.NORMAL));
      if (vedleggCount(soknad) != 0) {
         document.add(paragraph("Det er vedlagt en terminbekreftelse som er datert den " +
            terminbekreftelsesDato(soknad), LINE_TYPE.NORMAL));
      }

      document.add(blankLine());

      document.add(paragraph("Tilknytning til Norge", LINE_TYPE.HEADING));
      document.add(paragraph("De siste 12 månedene har jeg bodd i:", LINE_TYPE.NORMAL));
      document.add(paragraph(tidligereUtenlandsopphold(soknad), LINE_TYPE.NORMAL));
      document.add(paragraph("De neste 12 månedene skal jeg bo i " +
         countryName(soknad.getTilknytningNorge().isFremtidigOppholdNorge()), LINE_TYPE.NORMAL));
      document.add(paragraph("Og kommer på fødselstidpunket til å være i " +
         countryName(soknad.getTilknytningNorge().isOppholdNorgeNaa()), LINE_TYPE.NORMAL));

      document.add(blankLine());

      document.add(paragraph("Tilleggsopplysninger", LINE_TYPE.HEADING));
      document.add(paragraph(Optional.ofNullable(soknad.getTilleggsopplysninger()).orElse("Ingen"), LINE_TYPE.NORMAL));

      document.close();
      byte[] pdfBytes = baos.toByteArray();

      return pdfBytes;
   }

   private Paragraph paragraph(String txt, LINE_TYPE lineType) {
      return new Paragraph(new Chunk(txt, lineType == LINE_TYPE.HEADING ? headingFont : contentFont));
   }

   private Paragraph centeredParagraph(String txt, LINE_TYPE lineType) {
      Paragraph p = paragraph(txt, lineType);
      p.setAlignment(Element.ALIGN_CENTER);
      return p;
   }

   private Element separator() {
      Paragraph p = new Paragraph();
      DottedLineSeparator dottedline = new DottedLineSeparator();
      dottedline.setGap(2f);
      dottedline.setOffset(2);
      p.add(dottedline);
      p.add(blankLine());
      return p;
   }

   private Element blankLine() {
      Paragraph p = new Paragraph();
      p.add(Chunk.NEWLINE);
      return p;
   }

}
