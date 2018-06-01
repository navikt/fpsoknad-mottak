package no.nav.foreldrepenger.mottak.pdf;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.KjentForelder;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.UkjentForelder;
import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.felles.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.Fødsel;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.Utenlandsopphold;

@Service
public class EngangsstønadPDFGenerator extends AbstractPDFGenerator {

    public EngangsstønadPDFGenerator(MessageSource landkoder, MessageSource kvitteringstekster) {
        super(landkoder, kvitteringstekster, CountryCode.NO.toLocale());
    }

    public byte[] generate(Søknad søknad) {

        try {
            Engangsstønad stønad = Engangsstønad.class.cast(søknad.getYtelse());
            Medlemsskap medlemsskap = stønad.getMedlemsskap();
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            logo(document);
            document.add(center(heading(fromMessageSource("søknad_engang"))));
            søker(søknad, document);
            omBarn(stønad, document);

            if (erFremtidigFødsel(stønad)) {
                fødsel(søknad, stønad, document);
            }
            if (erFødt(stønad)) {
                født(søknad, stønad, document);
            }
            blankLine(document);

            medlemsskap(medlemsskap, document);
            fødselssted(medlemsskap, stønad, document);
            blankLine(document);

            omFar(stønad, document);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void født(Søknad søknad, Engangsstønad stønad, Document document) throws DocumentException {
        Fødsel ff = Fødsel.class.cast(stønad.getRelasjonTilBarn());
        document.add(
                regularParagraph(fromMessageSource("fødselsdato", dato(ff.getFødselsdato()))));
    }

    private void omFar(Engangsstønad stønad, Document document) throws DocumentException {
        AnnenForelder annenForelder = stønad.getAnnenForelder();

        if (annenForelder != null) {
            if (annenForelder instanceof KjentForelder && !((KjentForelder) annenForelder).hasId()) {
                return;
            }

            document.add(heading(fromMessageSource("omfar")));

            if (annenForelder instanceof NorskForelder) {
                norskForelder(document, annenForelder);
            }
            if (annenForelder instanceof UtenlandskForelder) {
                utenlandskForelder(document, annenForelder);
            }
            if (annenForelder instanceof UkjentForelder) {
                document.add(regularParagraph("Ukjent"));
            }
            blankLine(document);
        }
    }

    private void utenlandskForelder(Document document, AnnenForelder annenForelder) throws DocumentException {
        UtenlandskForelder utenlandsForelder = UtenlandskForelder.class.cast(annenForelder);
        document.add(regularParagraph(fromMessageSource("nasjonalitet",
                countryName(utenlandsForelder.getLand().getAlpha2(), utenlandsForelder.getLand().getName()))));
        navn(document, utenlandsForelder.getNavn());
        if (utenlandsForelder.getId() != null) {
            document.add(regularParagraph(fromMessageSource("utenlandskid", utenlandsForelder.getId())));
        }
    }

    private void norskForelder(Document document, AnnenForelder annenForelder) throws DocumentException {
        NorskForelder norskForelder = NorskForelder.class.cast(annenForelder);
        document.add(regularParagraph(fromMessageSource("nasjonalitet", "Norsk")));
        navn(document, norskForelder.getNavn());
        document.add(regularParagraph(fromMessageSource("fødselsnummer", norskForelder.getFnr().getFnr())));
    }

    private void fødselssted(Medlemsskap medlemsskap, Engangsstønad stønad, Document document) throws DocumentException {
        if (erFremtidigFødsel(stønad)) {
            document.add(regularParagraph(fromMessageSource("føderi",
                countryName(medlemsskap.getFramtidigOppholdsInfo().isFødselNorge()))));
        } else {
            Fødsel fødsel = Fødsel.class.cast(stønad.getRelasjonTilBarn());
            boolean inNorway = !stønad.getMedlemsskap().varUtenlands(fødsel.getFødselsdato().get(0));
            document.add(regularParagraph(fromMessageSource("fødtei", countryName(inNorway))));
        }

    }

    private void medlemsskap(Medlemsskap medlemsskap, Document document) throws DocumentException {
        document.add(heading(fromMessageSource("tilknytning")));
        List<String> siste12 = utenlandsOpphold(medlemsskap.getTidligereOppholdsInfo().getUtenlandsOpphold());
        if (siste12.size() == 1) {
            document.add(regularParagraph(fromMessageSource("siste12solo", siste12.get(0))));
        }
        else {
            document.add(regularParagraph(fromMessageSource("siste12")));
            document.add(bulletedList(siste12));
        }
        List<String> neste12 = utenlandsOpphold(medlemsskap.getFramtidigOppholdsInfo().getUtenlandsOpphold());
        if (neste12.size() == 1) {
            document.add(regularParagraph(fromMessageSource("neste12solo", neste12.get(0))));
        }
        else {
            document.add(regularParagraph(fromMessageSource("neste12")));
            document.add(bulletedList(neste12));
        }
    }

    private void omBarn(Engangsstønad stønad, Document document) throws DocumentException {
        document.add(heading(fromMessageSource("ombarn")));
        document.add(regularParagraph(
                fromMessageSource("gjelder", stønad.getRelasjonTilBarn().getAntallBarn())));
    }

    private static void søker(Søknad søknad, Document document) throws DocumentException {
        document.add(center(regularParagraph(søknad.getSøker().getFnr().getFnr())));
        String navn = navnToString(søknad.getSøker().getNavn());
        if (!navn.isEmpty()) {
            document.add(center(regularParagraph(navn)));
        }
        document.add(separator());
        blankLine(document);
    }

    private void navn(Document document, Navn navn) throws DocumentException {
        String n = navnToString(navn);
        if (!n.isEmpty()) {
            document.add(regularParagraph(fromMessageSource("navn", n)));
        }
    }

    private void fødsel(Søknad søknad, Engangsstønad stønad, Document document) throws DocumentException {
        FremtidigFødsel ff = FremtidigFødsel.class.cast(stønad.getRelasjonTilBarn());
        document.add(
                regularParagraph(fromMessageSource("termindato", dato(ff.getTerminDato()))));
        if (!søknad.getPåkrevdeVedlegg().isEmpty()) {
            document.add(regularParagraph(
                    fromMessageSource("termindatotekst", dato(ff.getUtstedtDato()))));
        }
    }

    private static void blankLine(Document document) throws DocumentException {
        document.add(blankLine());
    }

    private static boolean erFremtidigFødsel(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof FremtidigFødsel;
    }

    private static boolean erFødt(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof Fødsel;
    }

    private static String countryName(Boolean b) {
        return b ? "Norge" : "utlandet";
    }

    private List<String> utenlandsOpphold(List<Utenlandsopphold> opphold) {
        if (opphold.isEmpty()) {
            return Collections.singletonList(countryName(CountryCode.NO.getAlpha2()));
        }
        return opphold.stream()
                .map(this::formatOpphold)
                .collect(Collectors.toList());
    }

    private String formatOpphold(Utenlandsopphold opphold) {

        return countryName(opphold.getLand().getAlpha2(), opphold.getLand().getName())
                + ": "
                + dato(opphold.getVarighet().getFom()) + " - "
                + dato(opphold.getVarighet().getTom());
    }

}
