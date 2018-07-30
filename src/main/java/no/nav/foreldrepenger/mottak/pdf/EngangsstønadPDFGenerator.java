package no.nav.foreldrepenger.mottak.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import no.nav.foreldrepenger.mottak.domain.felles.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.util.Pair;

@Service
public class EngangsstønadPDFGenerator extends PDFGenerator {

    @Inject
    public EngangsstønadPDFGenerator(MessageSource landkoder, MessageSource kvitteringstekster) {
        this(new SøknadInfoFormatter(landkoder, kvitteringstekster, CountryCode.NO.toLocale()));
    }

    private EngangsstønadPDFGenerator(SøknadInfoFormatter infoFormatter) {
        super(infoFormatter);
    }

    public byte[] generate(Søknad søknad, Person søker) {
        Engangsstønad stønad = Engangsstønad.class.cast(søknad.getYtelse());
        Medlemsskap medlemsskap = stønad.getMedlemsskap();
        final PDPage page = newPage();
        try (PDDocument doc = new PDDocument();
                PDPageContentStream cos = new PDPageContentStream(doc, page);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            float y = PDFGenerator.calculateStartY();

            y -= header(søker, stønad, doc, cos, y);

            if (erFremtidigFødsel(stønad)) {
                y -= addLinesOfRegularText(fødsel(søknad, stønad), cos, y);
            }

            if (erFødt(stønad)) {
                y -= addLineOfRegularText(født(søknad, stønad), cos, y);
            }

            y -= addBlankLine();

            y -= tilknytning(medlemsskap, cos, y);

            y -= addBlankLine();
            y -= addLineOfRegularText(fødselssted(medlemsskap, stønad), cos, y);
            y -= addBlankLine();

            AnnenForelder annenForelder = stønad.getAnnenForelder();
            if (annenForelder != null && annenForelder instanceof KjentForelder
                    && ((KjentForelder) annenForelder).hasId()) {
                y -= addLeftHeading(infoFormatter.fromMessageSource("omfar"), cos, y);
                addLinesOfRegularText(omFar(stønad), cos, y);
            }

            doc.addPage(page);
            cos.close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error while creating pdf", ex);
        }
    }

    private float header(Person søker, Engangsstønad stønad, PDDocument doc, PDPageContentStream cos, float y)
            throws IOException {
        float startY = y;
        y -= addLogo(doc, cos, y);
        y -= addCenteredHeading(infoFormatter.fromMessageSource("søknad_engang"), cos, y);
        y -= addCenteredHeadings(søker(søker), cos, y);
        y -= addDividerLine(cos, y);
        y -= addBlankLine();
        y -= addLineOfRegularText(
                infoFormatter.fromMessageSource("gjelder", stønad.getRelasjonTilBarn().getAntallBarn()), cos, y);
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
        List<String> lines = new ArrayList<>(Arrays.asList(infoFormatter.fromMessageSource("nasjonalitet",
                infoFormatter.countryName(utenlandsForelder.getLand().getAlpha2(),
                        utenlandsForelder.getLand().getName())),
                infoFormatter.navn(utenlandsForelder.getNavn())));

        if (utenlandsForelder.getId() != null) {
            lines.add(infoFormatter.fromMessageSource("utenlandskid", utenlandsForelder.getId()));
        }
        return lines;
    }

    private List<String> norskForelder(AnnenForelder annenForelder) {
        NorskForelder norskForelder = NorskForelder.class.cast(annenForelder);
        List<String> lines = new ArrayList<>();
        lines.add(infoFormatter.fromMessageSource("nasjonalitet", "Norsk"));
        lines.add(infoFormatter.navn(norskForelder.getNavn()));
        lines.add(infoFormatter.fromMessageSource("fødselsnummer", norskForelder.getFnr().getFnr()));
        return lines;
    }

    private String fødselssted(Medlemsskap medlemsskap, Engangsstønad stønad) {
        if (erFremtidigFødsel(stønad)) {
            return infoFormatter.fromMessageSource("føderi",
                    infoFormatter.countryName(medlemsskap.getFramtidigOppholdsInfo().isFødselNorge()));
        }
        else {
            Fødsel fødsel = Fødsel.class.cast(stønad.getRelasjonTilBarn());
            boolean inNorway = !stønad.getMedlemsskap().varUtenlands(fødsel.getFødselsdato().get(0));
            return infoFormatter.fromMessageSource("fødtei", infoFormatter.countryName(inNorway));
        }

    }

    private Pair<List<String>, List<String>> medlemsskap(Medlemsskap medlemsskap) {
        return Pair.of(utenlandsOpphold(medlemsskap.getTidligereOppholdsInfo().getUtenlandsOpphold()),
                utenlandsOpphold(medlemsskap.getFramtidigOppholdsInfo().getUtenlandsOpphold()));
    }

    private List<String> søker(Person søker) {
        return Arrays.asList(søker.fnr.getFnr(),
                infoFormatter.navnToString(new Navn(søker.fornavn, søker.mellomnavn, søker.etternavn)));
    }

    private List<String> fødsel(Søknad søknad, Engangsstønad stønad) {
        FremtidigFødsel ff = FremtidigFødsel.class.cast(stønad.getRelasjonTilBarn());
        List<String> texts = new ArrayList<>();
        texts.add(infoFormatter.fromMessageSource("termindato", infoFormatter.dato(ff.getTerminDato())));
        if (!søknad.getPåkrevdeVedlegg().isEmpty()) {
            texts.add(infoFormatter.fromMessageSource("termindatotekst", infoFormatter.dato(ff.getUtstedtDato())));
        }
        return texts;
    }

    private String født(Søknad søknad, Engangsstønad stønad) {
        Fødsel ff = Fødsel.class.cast(stønad.getRelasjonTilBarn());
        return infoFormatter.fromMessageSource("fødselsdato", infoFormatter.dato(ff.getFødselsdato()));
    }

    private boolean erFremtidigFødsel(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof FremtidigFødsel;
    }

    private boolean erFødt(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof Fødsel;
    }

    private float tilknytning(Medlemsskap medlemsskap, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= addLeftHeading(infoFormatter.fromMessageSource("tilknytning"), cos, y);
        y -= addLineOfRegularText(infoFormatter.fromMessageSource("siste12"), cos, y);
        final Pair<List<String>, List<String>> medlemsPerioder = medlemsskap(medlemsskap);
        y -= addBulletList(medlemsPerioder.getFirst(), cos, y);
        y -= addBlankLine();
        y -= addLineOfRegularText(infoFormatter.fromMessageSource("neste12"), cos, y);
        y -= addBulletList(medlemsPerioder.getSecond(), cos, y);
        return startY - y;
    }

    private List<String> utenlandsOpphold(List<Utenlandsopphold> opphold) {
        if (opphold.isEmpty()) {
            return Collections.singletonList(infoFormatter.countryName(CountryCode.NO.getAlpha2()));
        }
        return opphold.stream()
                .map(this::formatOpphold)
                .collect(Collectors.toList());
    }

    private String formatOpphold(Utenlandsopphold opphold) {
        return infoFormatter.countryName(opphold.getLand().getAlpha2(), opphold.getLand().getName())
                + ": "
                + infoFormatter.dato(opphold.getVarighet().getFom()) + " - "
                + infoFormatter.dato(opphold.getVarighet().getTom());
    }

}
