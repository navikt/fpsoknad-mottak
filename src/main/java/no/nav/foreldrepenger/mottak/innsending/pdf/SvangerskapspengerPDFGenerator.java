package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.*;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper.SVANGERSKAPSPENGER;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

@Service
public class SvangerskapspengerPDFGenerator implements PDFGenerator {
    private static DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
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
                List<no.nav.foreldrepenger.mottak.domain.Arbeidsforhold> arbeidsforhold = oppslag.getArbeidsforhold();
                y -= renderer.addLeftHeading(textFormatter.fromMessageSource("tilrettelegging"), cos, y);
                Map<Arbeidsforhold, List<Tilrettelegging>> tilretteleggingsPerioder = tilretteleggingByArbeidsforhold(svp.getTilrettelegging());

                // type arbeidsforhold kommer i random rekkefølge
                for (Map.Entry<Arbeidsforhold, List<Tilrettelegging>> arb : tilretteleggingsPerioder.entrySet()) {
                   Arbeidsforhold ærbe = arb.getKey();
                   List<Tilrettelegging> tilrætte = sortertTilretteleggingsliste(arb.getValue());

                   PDPage scratch1 = newPage();
                   FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                   float startY = STARTY;
                   startY = header(søker, doc, scratchcos, startY);
                   float size = renderTilrettelegging(arbeidsforhold, ærbe, tilrætte, scratchcos, startY);
                   float behov = startY - size;
                   if (behov < y) {
                       scratchcos.close();
                       y = renderTilrettelegging(arbeidsforhold, ærbe, tilrætte, cos, y);
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

    private float renderTilrettelegging(List<no.nav.foreldrepenger.mottak.domain.Arbeidsforhold> arbeidsforhold, Arbeidsforhold ærbe, List<Tilrettelegging> tilrætte, FontAwareCos cos, float y) throws IOException {
        if (ærbe instanceof Virksomhet) {
            y -= renderer.addLineOfRegularText(
                virksomhetsnavn(arbeidsforhold, ((Virksomhet) ærbe).getOrgnr()), cos, y);
            y -= renderTilretteleggingsperioder(tilrætte, cos, y);
            y -= blankLine();
        }
        if (ærbe instanceof PrivatArbeidsgiver) {
            y -= renderer.addLineOfRegularText(txt("svp.privatarbeidsgiver"), cos, y);
            y -= renderTilretteleggingsperioder(tilrætte, cos, y);
            y -= blankLine();
        }
        if (ærbe instanceof SelvstendigNæringsdrivende) {
            y -= renderer.addLineOfRegularText(txt("svp.selvstendig"), cos, y);
            y -= renderTilretteleggingsperioder(tilrætte, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer", ((SelvstendigNæringsdrivende) ærbe).getRisikoFaktorer()), cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak", ((SelvstendigNæringsdrivende) ærbe).getTilretteleggingstiltak()), cos, y);
            y -= blankLine();
        }
        if (ærbe instanceof Frilanser) {
            y -= renderer.addLineOfRegularText(txt("svp.frilans"), cos, y);
            y -= renderTilretteleggingsperioder(tilrætte, cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.risikofaktorer", ((Frilanser) ærbe).getRisikoFaktorer()), cos, y);
            y -= renderer.addBulletPoint(INDENT, txt("svp.tiltak", ((Frilanser) ærbe).getTilretteleggingstiltak()), cos, y);
            y -= blankLine();
        }
        return y;
    }

    private Map<Arbeidsforhold, List<Tilrettelegging>> tilretteleggingByArbeidsforhold(List<Tilrettelegging> tilretteleggingsPerioder) {
        HashMap<Arbeidsforhold, List<Tilrettelegging>> grouped= new HashMap<>();
        for (Tilrettelegging periode : tilretteleggingsPerioder) {
            if (!grouped.containsKey(periode.getArbeidsforhold())) {
                List<Tilrettelegging> list = new ArrayList<>();
                list.add(periode);
                grouped.put(periode.getArbeidsforhold(), list);
            } else grouped.get(periode.getArbeidsforhold()).add(periode);
        }
        return grouped;
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

    private List<Tilrettelegging> sortertTilretteleggingsliste(List<Tilrettelegging> liste) {
        return liste.stream()
                .sorted(Comparator.comparing(Tilrettelegging::getBehovForTilretteleggingFom))
                .collect(Collectors.toList());
    }

    private float renderTilretteleggingsperioder(List<Tilrettelegging> perioder, FontAwareCos cos, float y) throws IOException {
        float startY = y;
        for (Tilrettelegging periode : perioder) {
            y -= renderer.addBulletPoint(INDENT,
                txt("svp.behovfra", DATE.format(periode.getBehovForTilretteleggingFom())), cos, y);
            if (periode instanceof HelTilrettelegging)
                y -= renderHelTilrettelegging((HelTilrettelegging) periode, cos, y);
            else if (periode instanceof DelvisTilrettelegging)
                y -= renderDelvisTilrettelegging((DelvisTilrettelegging) periode, cos, y);
            else if (periode instanceof IngenTilrettelegging)
                y -= renderIngenTilrettelegging((IngenTilrettelegging) periode, cos, y);
        }
        return startY - y;
    }

    private float renderIngenTilrettelegging(IngenTilrettelegging periode, FontAwareCos cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT, txt("svp.sluttearbeid", DATE.format(periode.getSlutteArbeidFom())), cos, y);
        y -= renderVedlegg(periode.getVedlegg(), cos, y);
        return startY - y;
    }

    private float renderDelvisTilrettelegging(DelvisTilrettelegging periode, FontAwareCos cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT, txt("svp.tilretteleggingfra", DATE.format(periode.getTilrettelagtArbeidFom())), cos, y);
        y -= renderer.addBulletPoint(INDENT, txt("svp.stillingsprosent", periode.getStillingsprosent().getProsent()), cos, y);
        y -= renderVedlegg(periode.getVedlegg(), cos, y);
        return startY - y;
    }

    private float renderHelTilrettelegging(HelTilrettelegging helTilrettelegging, FontAwareCos cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addBulletPoint(INDENT, txt("svp.tilretteleggingfra",
                DATE.format(helTilrettelegging.getTilrettelagtArbeidFom())), cos, y);
        y -= renderVedlegg(helTilrettelegging.getVedlegg(), cos, y);
        return startY - y;
    }

    private float renderVedlegg(List<String> vedleggRefs, FontAwareCos cos, float y) throws IOException {
        float startY = y;
        if (!vedleggRefs.isEmpty()) {
            y -= renderer.addBulletPoint(INDENT, txt("vedlegg1"), cos, y);
            for (String vedleggRef : vedleggRefs) {
                y -= renderer.addBulletPoint(INDENT * 2, vedleggRef, cos, y);
            }
        }
        return startY - y;
    }

    private String virksomhetsnavn(List<no.nav.foreldrepenger.mottak.domain.Arbeidsforhold> arbeidsgiver, String orgnr) {
        return arbeidsgiver.stream()
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
                txt("fødselsdato", DATE.format(svp.getFødselsdato())), cos, y);
            y -= renderer.addLineOfRegularText(
                txt("fødselmedtermin", DATE.format(svp.getTermindato())), cos, y);
        } else {
            y -= renderer.addLineOfRegularText(
                txt("svp_termindato", DATE.format(svp.getTermindato())), cos, y);
        }
        return startY - y;
    }

    private float header(Person søker, FontAwarePDDocument doc, FontAwareCos cos, float y)
        throws IOException {
        float startY = y;
        y -= renderer.addLogo(doc, cos, y);
        y -= renderer.addCenteredHeading(textFormatter.fromMessageSource("søknad_svp"), cos, y);
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

    private PDPage newPage() {
        return new PDPage(A4);
    }

    private float nesteSideStart(float headerSize, float behov) {
        return STARTY - behov - headerSize;
    }

    private FontAwareCos nySide(FontAwarePDDocument doc, FontAwareCos cos, PDPage scratch, FontAwareCos scratchcos) throws IOException {
        cos.close();
        doc.addPage(scratch);
        return scratchcos;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return SVANGERSKAPSPENGER;
    }
}
