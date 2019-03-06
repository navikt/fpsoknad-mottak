package no.nav.foreldrepenger.mottak.innsending.pdf;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.*;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.Pair;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

@Service
public class EngangsstønadPDFGenerator implements PDFGenerator {
    private final SøknadTextFormatter textFormatter;
    private final PDFElementRenderer renderer;

    @Inject
    public EngangsstønadPDFGenerator(SøknadTextFormatter textFormatter, PDFElementRenderer renderer) {
        this.textFormatter = textFormatter;
        this.renderer = renderer;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return new MapperEgenskaper(INITIELL_ENGANGSSTØNAD);
    }

    @Override
    public byte[] generate(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        Engangsstønad stønad = Engangsstønad.class.cast(søknad.getYtelse());
        Medlemsskap medlemsskap = stønad.getMedlemsskap();
        final PDPage page = newPage();
        try (FontAwarePDDocument doc = new FontAwarePDDocument();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            FontAwareCos cos = new FontAwareCos(doc, page);
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
            if (annenForelder.hasId()) {
                y -= renderer.addLeftHeading(textFormatter.fromMessageSource("omfar"), cos, y);
                renderer.addLinesOfRegularText(omFar(stønad), cos, y);
            }

            doc.addPage(page);
            cos.close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new PDFException("Kunne ikke lage PDF", e);
        }
    }

    private float omBarn(Person søker, Søknad søknad, Engangsstønad stønad, FontAwareCos cos, float y)
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

    private float header(Person søker, Engangsstønad stønad, FontAwarePDDocument doc, FontAwareCos cos, float y)
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
                textFormatter.countryName(utenlandsForelder.getLand(), utenlandsForelder.getLand().getName())),
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
            return textFormatter.fromMessageSource("terminføderi",
                    textFormatter.countryName(medlemsskap.landVedDato(stønad.getRelasjonTilBarn().relasjonsDato())),
                    stønad.getRelasjonTilBarn().getAntallBarn() > 1 ? "a" : "et");
        }
        else {
            Fødsel fødsel = Fødsel.class.cast(stønad.getRelasjonTilBarn());
            CountryCode land = stønad.getMedlemsskap().landVedDato(fødsel.getFødselsdato().get(0));
            return textFormatter.fromMessageSource("fødtei",
                    textFormatter.countryName(land),
                    fødsel.getAntallBarn() > 1 ? "a" : "et");
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
        texts.add(textFormatter.fromMessageSource("termindato", textFormatter.dato(ff.getTerminDato())));
        if (!søknad.getPåkrevdeVedlegg().isEmpty()) {
            texts.add(textFormatter.fromMessageSource("termindatotekst", textFormatter.dato(ff.getUtstedtDato())));
        }
        return texts;
    }

    private String født(Søknad søknad, Engangsstønad stønad) {
        Fødsel ff = Fødsel.class.cast(stønad.getRelasjonTilBarn());
        return textFormatter.fromMessageSource("fødselsdato", textFormatter.datoer(ff.getFødselsdato()));
    }

    private static boolean erFremtidigFødsel(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof FremtidigFødsel;
    }

    private static boolean erFødt(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof Fødsel;
    }

    private float tilknytning(Medlemsskap medlemsskap, FontAwareCos cos, float y) throws IOException {
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

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [textFormatter=" + textFormatter + ", renderer=" + renderer + "]";
    }

}
