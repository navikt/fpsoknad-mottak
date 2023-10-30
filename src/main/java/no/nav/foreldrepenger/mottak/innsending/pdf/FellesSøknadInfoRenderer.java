package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.util.stream.Collectors.joining;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000049;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000060;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.VedleggReferanse;
import no.nav.foreldrepenger.common.domain.felles.opptjening.EgenNæring;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Frilans;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Regnskapsfører;
import no.nav.foreldrepenger.common.domain.felles.opptjening.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@Component
public class FellesSøknadInfoRenderer {

    protected static final String ARBEIDSGIVER = "arbeidsgiver";

    private static final int INDENT = 20;
    private static final int INDENT_DOUBLE = INDENT * 2;

    private final PdfElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    public FellesSøknadInfoRenderer(PdfElementRenderer renderer, SøknadTextFormatter textFormatter) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
    }

    public float frilansOpptjeningForeldrepenger(Frilans frilans, FontAwareCos cos, float y) throws IOException {
        // bruker stilles spm om harInntektFraFosterhjem og nyOppstartet kun i fp
        return frilansOpptjening(frilans, cos, y, true);
    }

    public float frilansOpptjeningSvangerskapspenger(Frilans frilans, FontAwareCos cos, float y) throws IOException {
        return frilansOpptjening(frilans, cos, y, false);
    }

    private float frilansOpptjening(Frilans frilans, FontAwareCos cos, float y, boolean gjelderForeldrepenger) throws IOException {
        if (frilans == null) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("frilans"), cos, y);
        List<String> attributter = new ArrayList<>();
        if (frilans.jobberFremdelesSomFrilans()) {
            addIfSet(attributter, "frilanspågår", textFormatter.dato(frilans.periode().fom()));
        } else {
            attributter.add(txt("frilansavsluttet", textFormatter.dato(frilans.periode().fom())));
        }
        if (gjelderForeldrepenger) {
            attributter.add(txt("fosterhjem", jaNei(frilans.harInntektFraFosterhjem())));
            attributter.add(txt("nyoppstartet", jaNei(frilans.nyOppstartet())));
        }
        y -= renderer.addLinesOfRegularText(INDENT, attributter, cos, y);
        if (!frilans.frilansOppdrag().isEmpty()) {
            y -= renderer.addLineOfRegularText(INDENT, txt("oppdrag"), cos, y);
            var oppdrag = safeStream(frilans.frilansOppdrag())
                .map(o -> o.oppdragsgiver() + " " + textFormatter.periode(o.periode()))
                .toList();
            y -= renderer.addBulletList(INDENT_DOUBLE, oppdrag, cos, y);
            y -= PdfElementRenderer.BLANK_LINE;
        } else {
            y -= renderer.addLineOfRegularText(INDENT,txt("oppdrag") + ": Nei", cos, y);
        }
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    public float egneNæringerOpptjening(List<EgenNæring> egneNæringer, FontAwareCos cos, float y)
        throws IOException {
        if (CollectionUtils.isEmpty(egneNæringer)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("egennæring"), cos, y);
        for (var næring : egneNæringer(egneNæringer)) {
            y -= renderer.addLinesOfRegularText(INDENT, næring, cos, y);
            y -= PdfElementRenderer.BLANK_LINE;
        }
        return y;
    }

    public float arbeidsforholdOpptjening(List<EnkeltArbeidsforhold> arbeidsforhold, FontAwareCos cos, float y)
        throws IOException {
        if (CollectionUtils.isEmpty(arbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("arbeidsforhold"), cos, y);
        for (var forhold : sorterArbeidsforhold(arbeidsforhold)) {
            y -= renderer.addLinesOfRegularText(INDENT, arbeidsforhold(forhold), cos, y);
            y -= PdfElementRenderer.BLANK_LINE;
        }
        return y;
    }

    public float utenlandskeArbeidsforholdOpptjening(List<UtenlandskArbeidsforhold> utenlandskArbeidsforhold,
                                                     List<Vedlegg> vedlegg, FontAwareCos cos,
                                                     float y) throws IOException {
        if (CollectionUtils.isEmpty(utenlandskArbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("utenlandskarbeid"), cos, y);
        for (var forhold : sorterUtelandske(utenlandskArbeidsforhold)) {
            y -= renderer.addLinesOfRegularText(INDENT, utenlandskeArbeidsforhold(forhold), cos, y);
            y = renderVedlegg(vedlegg, forhold.vedlegg(), "vedleggutenlandskarbeid", cos, y);
        }
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    protected float renderVedlegg(List<Vedlegg> vedlegg, List<VedleggReferanse> vedleggRefs, String keyIfAnnet,
                                FontAwareCos cos,
                                float y) throws IOException {
        if (!vedleggRefs.isEmpty()) {
            y -= renderer.addLineOfRegularText(INDENT, txt("vedlegg1"), cos, y);
        }
        for (var id : vedleggRefs) {
            var details = safeStream(vedlegg)
                .filter(s -> id.referanse().equals(s.getId()))
                .findFirst();
            if (details.isPresent()) {
                var beskrivelse = vedleggsBeskrivelse(keyIfAnnet, details.get());
                y -= renderer.addBulletPoint(INDENT,
                    txt("vedlegg2", beskrivelse, cap(details.get().getInnsendingsType().name())),
                    cos, y);
            } else {
                // Never, hopefully
                y -= renderer.addBulletPoint(INDENT, txt("vedlegg2", "vedlegg"), cos, y);
            }
        }
        return y;
    }

    List<String> arbeidsforhold(EnkeltArbeidsforhold arbeidsforhold) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, ARBEIDSGIVER, arbeidsforhold.arbeidsgiverNavn());
        addIfSet(attributter, "fom", arbeidsforhold.from());
        addIfSet(attributter, "tom", arbeidsforhold.to());
        if (arbeidsforhold.stillingsprosent() != null) {
            attributter.add(txt("stillingsprosent", prosentFra(arbeidsforhold.stillingsprosent())));
        }
        return attributter;
    }

    private static List<EnkeltArbeidsforhold> sorterArbeidsforhold(List<EnkeltArbeidsforhold> arbeidsforhold) {
        return safeStream(arbeidsforhold)
            .sorted(Comparator.comparing(EnkeltArbeidsforhold::from))
            .toList();
    }

    private static List<UtenlandskArbeidsforhold> sorterUtelandske(List<UtenlandskArbeidsforhold> arbeidsforhold) {
        return safeStream(arbeidsforhold)
            .sorted(Comparator.comparing(utenlandskArbeidsforhold -> utenlandskArbeidsforhold.periode().fom()))
            .toList();
    }

    private List<String> utenlandskeArbeidsforhold(UtenlandskArbeidsforhold ua) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, ARBEIDSGIVER, ua.arbeidsgiverNavn());
        addIfSet(attributter, "fom", ua.periode().fom());
        addIfSet(attributter, "tom", ua.periode().tom());
        addIfSet(attributter, "virksomhetsland", ua.land());
        return attributter;
    }

    private List<List<String>> egneNæringer(List<EgenNæring> egenNæring) {
        return safeStream(egenNæring)
            .map(this::egenNæring)
            .toList();
    }

    private List<String> egenNæring(EgenNæring næring) {
        List<String> attributter = new ArrayList<>();
        if (næring.orgNummer() != null) {
            addIfSet(attributter, "orgnummer", næring.orgNummer().value());
        }

        addIfSet(attributter, "virksomhetsnavn", næring.orgName());
        addIfSet(attributter, "registrertiland", næring.registrertILand());
        attributter.add(txt("egennæringtyper", næring.vedlegg().size() > 1 ? "r" : "",
            safeStream(næring.virksomhetsTyper())
                .map(v -> textFormatter.capitalize(v.toString()))
                .collect(joining(","))));
        if (næring.periode().tom() == null) {
            addIfSet(attributter, "egennæringpågår", textFormatter.dato(næring.periode().fom()));
        } else {
            attributter.add(txt("egennæringavsluttet", næring.periode().fom(),
                textFormatter.dato(næring.periode().tom())));
        }
        if (næring.stillingsprosent() != null) {
            attributter.add(txt("stillingsprosent", prosentFra(næring.stillingsprosent())));
        }
        attributter.add(txt("nyligyrkesaktiv", jaNei(næring.erNyIArbeidslivet())));
        attributter.add(txt("varigendring", jaNei(næring.erVarigEndring())));
        addIfSet(attributter, "egennæringbeskrivelseendring", næring.beskrivelseEndring());
        addIfSet(attributter, "egennæringendringsdato", næring.endringsDato());
        if (næring.erNyOpprettet() || næring.erVarigEndring()) {
            addMoneyIfSet(attributter, "egennæringbruttoinntekt", næring.næringsinntektBrutto());
        }
        if (næring.erNyOpprettet()) {
            attributter.add(txt("nystartetvirksomhet", jaNei(true)));
            addIfSet(attributter, "egennæringoppstartsdato", næring.oppstartsDato());
        }
        var rf = regnskapsfører(næring);
        if (rf != null) {
            if (rf.telefon() != null) {
                attributter.add(
                    txt("regnskapsførertelefon", rf.navn(), rf.telefon(), jaNei(næring.nærRelasjon())));
            } else {
                attributter.add(txt("regnskapsfører", rf.navn(), jaNei(næring.nærRelasjon())));
            }
        }
        return attributter;
    }

    private static Regnskapsfører regnskapsfører(EgenNæring næring) {
        if (næring == null || CollectionUtils.isEmpty(næring.regnskapsførere())) {
            return null;
        }
        return næring.regnskapsførere().get(0);
    }

    protected String jaNei(boolean value) {
        return textFormatter.yesNo(value);
    }

    protected String cap(String name) {
        return textFormatter.capitalize(name);
    }

    protected void addIfSet(List<String> attributter, String key, String value) {
        if (value != null) {
            attributter.add(txt(key, value));
        }
    }

    protected void addIfSet(List<String> attributter, String key, LocalDate dato) {
        if (dato != null) {
            attributter.add(txt(key, textFormatter.dato(dato)));
        }
    }

    protected void addIfSet(List<String> attributter, String key, Optional<LocalDate> dato) {
        dato.ifPresent(localDate -> attributter.add(txt(key, textFormatter.dato(localDate))));
    }

    protected void addMoneyIfSet(List<String> attributter, String key, Long sum) {
        if (sum != null && sum > 0L) {
            attributter.add(txt(key, String.valueOf(sum)));
        }
    }

    protected void addIfSet(List<String> attributter, String key, CountryCode land) {
        if (land != null) {
            attributter.add(txt(key, textFormatter.countryName(land)));
        }
    }

    protected static double prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
            .map(ProsentAndel::prosent)
            .orElse(0d);
    }

    protected String txt(String key, Object... values) {
        return textFormatter.fromMessageSource(key, values);
    }

    protected String vedleggsBeskrivelse(String key, Vedlegg vedlegg) {
        return erAnnenDokumentType(vedlegg) ? txt(key) : vedlegg.getBeskrivelse();
    }

    protected static boolean erAnnenDokumentType(Vedlegg vedlegg) {
        return vedlegg.getDokumentType().equals(I000060) || vedlegg.getDokumentType().equals(I000049);
    }
}
