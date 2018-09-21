package no.nav.foreldrepenger.mottak.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

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
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.util.Pair;

@Service
public class EngangsstønadPDFGenerator {
    private final SøknadTextFormatter textFormatter;
    private final PDFElementRenderer renderer;

    @Inject
    public EngangsstønadPDFGenerator(MessageSource landkoder, MessageSource kvitteringstekster) {
        this(new SøknadTextFormatter(landkoder, kvitteringstekster, CountryCode.NO));
    }

    private EngangsstønadPDFGenerator(SøknadTextFormatter textFormatter) {
        this(textFormatter, new PDFElementRenderer());
    }

    private EngangsstønadPDFGenerator(SøknadTextFormatter textFormatter, PDFElementRenderer renderer) {
        this.textFormatter = textFormatter;
        this.renderer = renderer;
    }

    public byte[] generate(Søknad søknad, Person søker) {
        Engangsstønad stønad = Engangsstønad.class.cast(søknad.getYtelse());
        Medlemsskap medlemsskap = stønad.getMedlemsskap();
        final PDPage page = renderer.newPage();
        try (PDDocument doc = new PDDocument();
                PDPageContentStream cos = new PDPageContentStream(doc, page);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            float y = PDFElementRenderer.calculateStartY();

            y -= header(søker, stønad, doc, cos, y);
            y -= renderer.addBlankLine();

            y -= omBarn(søker, søknad, stønad, cos, y);
            y -= renderer.addBlankLine();

            y -= tilknytning(medlemsskap, cos, y);
            y -= renderer.addBlankLine();

            y -= renderer.addLineOfRegularText(fødselssted(medlemsskap, stønad), cos, y);
            y -= renderer.addBlankLine();

            AnnenForelder annenForelder = stønad.getAnnenForelder();
            if (annenForelder != null && annenForelder instanceof KjentForelder
                    && ((KjentForelder) annenForelder).hasId()) {
                y -= renderer.addLeftHeading(textFormatter.fromMessageSource("omfar"), cos, y);
                renderer.addLinesOfRegularText(omFar(stønad), cos, y);
            }

            doc.addPage(page);
            cos.close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error while creating pdf", ex);
        }
    }

    private float omBarn(Person søker, Søknad søknad, Engangsstønad stønad, PDPageContentStream cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("ombarn"), cos, y);
        y -= renderer.addLineOfRegularText(
                textFormatter.fromMessageSource("gjelder", stønad.getRelasjonTilBarn().getAntallBarn()), cos, y);

        if (erFremtidigFødsel(stønad)) {
            y -= renderer.addLinesOfRegularText(fødsel(søknad, stønad), cos, y);
        }

        if (erFødt(stønad)) {
            y -= renderer.addLineOfRegularText(født(søknad, stønad), cos, y);
        }
        return startY - y;
    }

    private float header(Person søker, Engangsstønad stønad, PDDocument doc, PDPageContentStream cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addLogo(doc, cos, y);
        y -= renderer.addCenteredHeading(textFormatter.fromMessageSource("søknad_engang"), cos, y);
        y -= renderer.addCenteredHeadings(søker(søker), cos, y);
        y -= renderer.addDividerLine(cos, y);
        return startY - y;
    }

    private List<String> omFar(Engangsstønad stønad) {
        AnnenForelder annenForelder = stønad.getAnnenForelder();
        List<String> farInfo = new ArrayList<>();

        if (annenForelder instanceof NorskForelder) {
            farInfo.addAll(norskForelder(annenForelder));
        }
        if (annenForelder instanceof UtenlandskForelder) {
            farInfo.addAll(utenlandskForelder(annenForelder));
        }
        if (annenForelder instanceof UkjentForelder) {
            farInfo.add("Ukjent");
        }
        return farInfo;
    }

    private List<String> utenlandskForelder(AnnenForelder annenForelder) {
        UtenlandskForelder utenlandsForelder = UtenlandskForelder.class.cast(annenForelder);
        List<String> lines = new ArrayList<>(Arrays.asList(textFormatter.fromMessageSource("nasjonalitet",
                textFormatter.countryName(utenlandsForelder.getLand().getAlpha2(),
                        utenlandsForelder.getLand().getName())),
                textFormatter.navn(utenlandsForelder.getNavn())));

        if (utenlandsForelder.getId() != null) {
            lines.add(textFormatter.fromMessageSource("utenlandskid", utenlandsForelder.getId()));
        }
        return lines;
    }

    private List<String> norskForelder(AnnenForelder annenForelder) {
        NorskForelder norskForelder = NorskForelder.class.cast(annenForelder);
        List<String> lines = new ArrayList<>();
        lines.add(textFormatter.fromMessageSource("nasjonalitet", "Norsk"));
        lines.add(textFormatter.navn(norskForelder.getNavn()));
        lines.add(textFormatter.fromMessageSource("fødselsnummer", norskForelder.getFnr().getFnr()));
        return lines;
    }

    private String fødselssted(Medlemsskap medlemsskap, Engangsstønad stønad) {
        if (erFremtidigFødsel(stønad)) {
            return textFormatter.fromMessageSource("føderi",
                    textFormatter.countryName(medlemsskap.getFramtidigOppholdsInfo().isFødselNorge()));
        }
        else {
            Fødsel fødsel = Fødsel.class.cast(stønad.getRelasjonTilBarn());
            boolean inNorway = !stønad.getMedlemsskap().varUtenlands(fødsel.getFødselsdato().get(0));
            return textFormatter.fromMessageSource("fødtei", textFormatter.countryName(inNorway));
        }

    }

    private Pair<List<String>, List<String>> medlemsskap(Medlemsskap medlemsskap) {
        return Pair.of(textFormatter.utenlandsOpphold(medlemsskap.getTidligereOppholdsInfo().getUtenlandsOpphold()),
                textFormatter.utenlandsOpphold(medlemsskap.getFramtidigOppholdsInfo().getUtenlandsOpphold()));
    }

    private List<String> søker(Person søker) {
        return Arrays.asList(søker.fnr.getFnr(),
                textFormatter.navn(new Navn(søker.fornavn, søker.mellomnavn, søker.etternavn)));
    }

    private List<String> fødsel(Søknad søknad, Engangsstønad stønad) {
        FremtidigFødsel ff = FremtidigFødsel.class.cast(stønad.getRelasjonTilBarn());
        List<String> texts = new ArrayList<>();
        texts.add(textFormatter.fromMessageSource("termindato", textFormatter.date(ff.getTerminDato())));
        if (!søknad.getPåkrevdeVedlegg().isEmpty()) {
            texts.add(textFormatter.fromMessageSource("termindatotekst", textFormatter.date(ff.getUtstedtDato())));
        }
        return texts;
    }

    private String født(Søknad søknad, Engangsstønad stønad) {
        Fødsel ff = Fødsel.class.cast(stønad.getRelasjonTilBarn());
        return textFormatter.fromMessageSource("fødselsdato", textFormatter.dates(ff.getFødselsdato()));
    }

    private static boolean erFremtidigFødsel(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof FremtidigFødsel;
    }

    private static boolean erFødt(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof Fødsel;
    }

    private float tilknytning(Medlemsskap medlemsskap, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("tilknytning"), cos, y);
        y -= renderer.addLineOfRegularText(textFormatter.fromMessageSource("siste12"), cos, y);
        final Pair<List<String>, List<String>> medlemsPerioder = medlemsskap(medlemsskap);
        y -= renderer.addBulletList(medlemsPerioder.getFirst(), cos, y);
        y -= renderer.addBlankLine();
        y -= renderer.addLineOfRegularText(textFormatter.fromMessageSource("neste12"), cos, y);
        y -= renderer.addBulletList(medlemsPerioder.getSecond(), cos, y);
        return startY - y;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [textFormatter=" + textFormatter + ", renderer=" + renderer + "]";
    }

}
