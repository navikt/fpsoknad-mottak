package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000060;
import static no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper.SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Frilanser;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@Service
public class SvangerskapspengerPDFGenerator implements PDFGenerator {
    private static DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static DateTimeFormatter DATEFMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final float STARTY = PDFElementRenderer.calculateStartY();
    private static final int INDENT = 20;

    private final PDFElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;
    private final Oppslag oppslag;

    @Inject
    public SvangerskapspengerPDFGenerator(PDFElementRenderer renderer,
            SøknadTextFormatter textFormatter,
            Oppslag oppslag) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
        this.oppslag = oppslag;
    }

    @Override
    public byte[] generate(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        Svangerskapspenger svp = (Svangerskapspenger) søknad.getYtelse();

        try (FontAwarePDDocument doc = new FontAwarePDDocument();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = newPage();
            doc.addPage(page);
            FontAwareCos cos = new FontAwareCos(doc, page);
            float y = STARTY;
            y -= header(søker, doc, cos, y);
            float headerSize = STARTY - y;

            y -= omBarn(svp, cos, y);
            y -= blankLine();

            if (!svp.getTilrettelegging().isEmpty()) {
                List<no.nav.foreldrepenger.mottak.domain.Arbeidsforhold> arbeidsgivere = oppslag.getArbeidsforhold();
                y -= renderer.addLeftHeading(textFormatter.fromMessageSource("tilrettelegging"), cos, y);
                Map<Arbeidsforhold, List<Tilrettelegging>> tilretteleggingsPerioder = tilretteleggingByArbeidsforhold(
                        svp.getTilrettelegging());

                // type arbeidsforhold kommer i random rekkefølge
                for (Map.Entry<Arbeidsforhold, List<Tilrettelegging>> arb : tilretteleggingsPerioder.entrySet()) {
                    Arbeidsforhold arbeidsforhold = arb.getKey();
                    List<Tilrettelegging> tilrettelegging = sortertTilretteleggingsliste(arb.getValue());

                    PDPage scratch1 = newPage();
                    FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                    float startY = STARTY;
                    startY = header(søker, doc, scratchcos, startY);
                    float size = renderTilrettelegging(arbeidsgivere, arbeidsforhold, tilrettelegging, søknad.getVedlegg(), scratchcos,
                            startY);
                    float behov = startY - size;
                    if (behov < y) {
                        scratchcos.close();
                        y = renderTilrettelegging(arbeidsgivere, arbeidsforhold, tilrettelegging, søknad.getVedlegg(), cos, y);
                    }
                    else {
                        cos = nySide(doc, cos, scratch1, scratchcos);
                        y = nesteSideStart(headerSize, behov);
                    }
                }
            }

            if (svp.getMedlemsskap() != null) {
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float startY = STARTY;
                startY = header(søker, doc, scratchcos, startY);
                float size = renderMedlemskap(svp.getMedlemsskap(), scratchcos, startY);
                float behov = startY - size;
                if (behov < y) {
                    scratchcos.close();
                    y = renderMedlemskap(svp.getMedlemsskap(), cos, y);
                }
                else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = nesteSideStart(headerSize, behov);
                }
            }
            cos.close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UnexpectedInputException("Kunne ikke lage PDF", e);
        }
    }

    private float renderTilrettelegging(List<no.nav.foreldrepenger.mottak.domain.Arbeidsforhold> arbeidsgivere,
                                        Arbeidsforhold arbeidsforhold, List<Tilrettelegging> tilrettelegging,
                                        List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        if (arbeidsforhold instanceof Virksomhet) {
            y -= renderer.addLineOfRegularText(
                virksomhetsnavn(arbeidsgivere, Virksomhet.class.cast(arbeidsforhold).getOrgnr()), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof PrivatArbeidsgiver) {
            y -= renderer.addLineOfRegularText(txt("svp.privatarbeidsgiver"), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof SelvstendigNæringsdrivende) {
            y -= renderer.addLineOfRegularText(txt("svp.selvstendig"), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer",
                SelvstendigNæringsdrivende.class.cast(arbeidsforhold).getRisikoFaktorer()),
                cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak",
                SelvstendigNæringsdrivende.class.cast(arbeidsforhold).getTilretteleggingstiltak()),
                cos, y);
            y -= blankLine();
        }
        if (arbeidsforhold instanceof Frilanser) {
            y -= renderer.addLineOfRegularText(txt("svp.frilans"), cos, y);
            y -= renderTilretteleggingsperioder(tilrettelegging, vedlegg, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer",
                Frilanser.class.cast(arbeidsforhold).getRisikoFaktorer()), cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak",
                Frilanser.class.cast(arbeidsforhold).getTilretteleggingstiltak()), cos, y);
            y -= blankLine();
        }
        return y;
    }

    private static Map<Arbeidsforhold, List<Tilrettelegging>> tilretteleggingByArbeidsforhold(
            List<Tilrettelegging> tilretteleggingsPerioder) {
        Map<Arbeidsforhold, List<Tilrettelegging>> tilretteleggingByArbeidsforhold = new HashMap<>();
        tilretteleggingsPerioder.forEach(tp -> tilretteleggingByArbeidsforhold
                .computeIfAbsent(tp.getArbeidsforhold(), key -> new ArrayList<>())
                .add(tp));
        return tilretteleggingByArbeidsforhold;
    }

    private float renderMedlemskap(Medlemsskap medlemsskap, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("medlemsskap"), cos, y);
        TidligereOppholdsInformasjon tidligereOpphold = medlemsskap.getTidligereOppholdsInfo();
        FramtidigOppholdsInformasjon framtidigeOpphold = medlemsskap.getFramtidigOppholdsInfo();

        y -= renderer.addLineOfRegularText(txt("siste12") +
                (tidligereOpphold.isBoddINorge() ? " Norge" : ":"), cos, y);
        if (!tidligereOpphold.getUtenlandsOpphold().isEmpty()) {
            y -= renderer.addBulletList(textFormatter.utenlandsOpphold(tidligereOpphold.getUtenlandsOpphold()),
                    cos, y);
        }
        y -= renderer.addLineOfRegularText(txt("neste12") +
                (framtidigeOpphold.isNorgeNeste12() ? " Norge" : ":"), cos, y);
        if (!framtidigeOpphold.getUtenlandsOpphold().isEmpty()) {
            y -= renderer.addBulletList(textFormatter.utenlandsOpphold(framtidigeOpphold.getUtenlandsOpphold()),
                    cos,
                    y);
        }
        return y;
    }

    private static List<Tilrettelegging> sortertTilretteleggingsliste(List<Tilrettelegging> liste) {
        return safeStream(liste)
                .sorted(Comparator.comparing(Tilrettelegging::getBehovForTilretteleggingFom))
                .collect(Collectors.toList());
    }

    private float renderTilretteleggingsperioder(List<Tilrettelegging> perioder,
                                                 List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        for (Tilrettelegging periode : perioder) {
            y -= renderer.addBulletPoint(INDENT,
                    txt("svp.behovfra", DATEFMT.format(periode.getBehovForTilretteleggingFom())), cos, y);
            if (periode instanceof HelTilrettelegging)
                y -= renderHelTilrettelegging(HelTilrettelegging.class.cast(periode), vedlegg, cos, y);
            else if (periode instanceof DelvisTilrettelegging)
                y -= renderDelvisTilrettelegging(DelvisTilrettelegging.class.cast(periode), vedlegg, cos, y);
            else if (periode instanceof IngenTilrettelegging)
                y -= renderIngenTilrettelegging(IngenTilrettelegging.class.cast(periode), cos, y);
        }
        return startY - y;
    }

    private float renderIngenTilrettelegging(IngenTilrettelegging periode, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.sluttearbeid", DATEFMT.format(periode.getSlutteArbeidFom())), cos, y);
        //y -= renderVedlegg(vedlegg, .getVedlegg(), cos, y);
        return startY - y;
    }

    private float renderDelvisTilrettelegging(DelvisTilrettelegging periode,
                                              List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.tilretteleggingfra", DATEFMT.format(periode.getTilrettelagtArbeidFom())), cos, y);
        y -= renderer.addBulletPoint(INDENT,
                txt("svp.stillingsprosent", periode.getStillingsprosent().getProsent()), cos, y);
        y -= renderVedlegg(vedlegg, periode.getVedlegg(), "fiksfaks", cos, y);
        return startY - y;
    }

    private float renderHelTilrettelegging(HelTilrettelegging periode,
                                           List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT, txt("svp.tilretteleggingfra",
                DATEFMT.format(periode.getTilrettelagtArbeidFom())), cos, y);
        y -= renderVedlegg(vedlegg, periode.getVedlegg(), "fiksfaks", cos, y);
        return startY - y;
    }

    private float renderVedlegg(List<Vedlegg> vedlegg, List<String> vedleggRefs, String keyIfAnnet,
                                FontAwareCos cos, float y) throws IOException {
        float startY = y;
        if (!vedleggRefs.isEmpty())
            y -= renderer.addBulletPoint(INDENT, txt("vedlegg1"), cos, y);
        for (String vedleggRef : vedleggRefs) {
            Optional<Vedlegg> details = safeStream(vedlegg)
                .filter(s -> vedleggRef.equals(s.getId()))
                .findFirst();
            if (details.isPresent()) {
                String beskrivelse = vedleggsBeskrivelse(keyIfAnnet, details.get());
                y -= renderer.addBulletPoint(INDENT * 2,
                    txt("vedlegg2", beskrivelse, details.get().getInnsendingsType().name()),
                    cos, y);
            }
            else {
                // Never, hopefully
                y -= renderer.addBulletPoint(INDENT * 2, txt("vedlegg2", "vedlegg"), cos, y);
            }
        }
        return startY - y;
    }

    private String vedleggsBeskrivelse(String key, Vedlegg vedlegg) {
        return erAnnenDokumentType(vedlegg) ? txt(key) : vedlegg.getBeskrivelse();
    }

    private static boolean erAnnenDokumentType(Vedlegg vedlegg) {
        return vedlegg.getDokumentType().equals(I000060);
    }

    private String virksomhetsnavn(List<no.nav.foreldrepenger.mottak.domain.Arbeidsforhold> arbeidsgivere,
            String orgnr) {
        return safeStream(arbeidsgivere)
                .filter(arb -> arb.getArbeidsgiverId().equals(orgnr))
                .findFirst()
                .map(arb -> arb.getArbeidsgiverNavn())
                .orElse(txt("arbeidsgiverIkkeFunnet", orgnr));
    }

    private float omBarn(Svangerskapspenger svp, FontAwareCos cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("ombarn"), cos, y);
        if (svp.getFødselsdato() != null) {
            y -= renderer.addLineOfRegularText(
                    txt("fødselsdato", DATEFMT.format(svp.getFødselsdato())), cos, y);
            y -= renderer.addLineOfRegularText(
                    txt("fødselmedtermin", DATEFMT.format(svp.getTermindato())), cos, y);
        }
        else {
            y -= renderer.addLineOfRegularText(
                    txt("svp.termindato", DATEFMT.format(svp.getTermindato())), cos, y);
        }
        return startY - y;
    }

    private float header(Person søker, FontAwarePDDocument doc, FontAwareCos cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addLogo(doc, cos, y);
        y -= renderer.addCenteredHeading(textFormatter.fromMessageSource("svp.søknad"), cos, y);
        y -= renderer.addCenteredRegular(
                textFormatter.fromMessageSource("mottatt", FMT.format(LocalDateTime.now())), cos, y);
        y -= renderer.addCenteredRegulars(søker(søker), cos, y);
        y -= renderer.addDividerLine(cos, y);
        return startY - y;
    }

    private List<String> søker(Person søker) {
        return Arrays.asList(
                textFormatter.navn(new Navn(søker.fornavn, søker.mellomnavn, søker.etternavn)),
                textFormatter.fromMessageSource("fødselsnummer", søker.fnr.getFnr()));
    }

    private String txt(String key, Object... values) {
        return textFormatter.fromMessageSource(key, values);
    }

    private float blankLine() {
        return renderer.addBlankLine();
    }

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    private static float nesteSideStart(float headerSize, float behov) {
        return STARTY - behov - headerSize;
    }

    private static FontAwareCos nySide(FontAwarePDDocument doc, FontAwareCos cos, PDPage scratch, FontAwareCos scratchcos)
            throws IOException {
        cos.close();
        doc.addPage(scratch);
        return scratchcos;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return SVANGERSKAPSPENGER;
    }
}
