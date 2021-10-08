package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.util.stream.Collectors.joining;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000049;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000060;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.opptjening.EgenNæring;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Frilans;
import no.nav.foreldrepenger.common.domain.felles.opptjening.NorskOrganisasjon;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Regnskapsfører;
import no.nav.foreldrepenger.common.domain.felles.opptjening.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.common.domain.felles.opptjening.UtenlandskOrganisasjon;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@Component
public class SvangerskapspengerInfoRenderer {
    private static final Logger LOG = LoggerFactory.getLogger(SvangerskapspengerInfoRenderer.class);
    private static final int INDENT = 20;
    private final PdfElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    public SvangerskapspengerInfoRenderer(PdfElementRenderer renderer, SøknadTextFormatter textFormatter) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
    }

    float frilansOpptjening(Frilans frilans, FontAwareCos cos, float y) throws IOException {
        if (frilans == null) {
            return y;
        }
        y = frilans(frilans, cos, y);
        return y;
    }

    private float frilans(Frilans frilans, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("frilans"), cos, y);
        List<String> attributter = new ArrayList<>();
        if (frilans.getPeriode().tom() == null) {
            addIfSet(attributter, "frilanspågår", textFormatter.dato(frilans.getPeriode().fom()));
        } else {
            attributter.add(txt("frilansavsluttet", textFormatter.dato(frilans.getPeriode().fom()),
                    textFormatter.dato(frilans.getPeriode().tom())));
        }
        attributter.add(txt("fosterhjem", jaNei(frilans.isHarInntektFraFosterhjem())));
        attributter.add(txt("nyoppstartet", jaNei(frilans.isNyOppstartet())));
        y -= renderer.addLinesOfRegularText(attributter, cos, y);
        if (!frilans.getFrilansOppdrag().isEmpty()) {
            y -= renderer.addLineOfRegularText(txt("oppdrag"), cos, y);
            List<String> oppdrag = safeStream(frilans.getFrilansOppdrag())
                    .map(o -> o.oppdragsgiver() + " " + textFormatter.periode(o.periode()))
                    .toList();
            y -= renderer.addBulletList(INDENT, oppdrag, cos, y);
            y -= PdfElementRenderer.BLANK_LINE;
        } else {
            y -= renderer.addLineOfRegularText(txt("oppdrag") + ": Nei", cos, y);
        }
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private String txt(String key, Object... values) {
        return textFormatter.fromMessageSource(key, values);
    }

    private String jaNei(boolean value) {
        return textFormatter.yesNo(value);
    }

    private static Regnskapsfører regnskapsfører(EgenNæring næring) {
        if (næring == null || CollectionUtils.isEmpty(næring.getRegnskapsførere())) {
            return null;
        }
        return næring.getRegnskapsførere().get(0);
    }

    private void addIfSet(List<String> attributter, String key, String value) {
        if (value != null) {
            attributter.add(txt(key, value));
        }
    }

    private void addIfSet(List<String> attributter, String key, LocalDate dato) {
        if (dato != null) {
            attributter.add(txt(key, textFormatter.dato(dato)));
        }
    }

    private void addIfSet(List<String> attributter, String key, Optional<LocalDate> dato) {
        if (dato.isPresent()) {
            attributter.add(txt(key, textFormatter.dato(dato.get())));
        }
    }

    private void addIfSet(List<String> attributter, String key, CountryCode land) {
        if (land != null) {
            attributter.add(txt(key, textFormatter.countryName(land)));
        }
    }

    private void addMoneyIfSet(List<String> attributter, String key, Long sum) {
        if (sum != null && sum > 0L) {
            attributter.add(txt(key, String.valueOf(sum)));
        }
    }

    float egneNæringerOpptjening(List<EgenNæring> egneNæringer, FontAwareCos cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(egneNæringer)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("egennæring"), cos, y);
        for (List<String> næring : egneNæringer(egneNæringer)) {
            y -= renderer.addLinesOfRegularText(næring, cos, y);
            y -= PdfElementRenderer.BLANK_LINE;
        }
        return y;
    }

    float arbeidsforholdOpptjening(List<EnkeltArbeidsforhold> arbeidsforhold, FontAwareCos cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(arbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("arbeidsforhold"), cos, y);
        for (EnkeltArbeidsforhold forhold : sorterArbeidsforhold(arbeidsforhold)) {
            y -= renderer.addLinesOfRegularText(INDENT, arbeidsforhold(forhold), cos, y);
            y -= PdfElementRenderer.BLANK_LINE;
        }
        return y;
    }

    private static List<EnkeltArbeidsforhold> sorterArbeidsforhold(List<EnkeltArbeidsforhold> arbeidsforhold) {
        Collections.sort(arbeidsforhold, (o1, o2) -> {
            if (o1.getFrom() != null && o2.getFrom() != null) {
                return o1.getFrom().compareTo(o2.getFrom());
            }
            return 0;
        });
        return arbeidsforhold;
    }

    List<String> arbeidsforhold(EnkeltArbeidsforhold arbeidsforhold) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "arbeidsgiver", arbeidsforhold.getArbeidsgiverNavn());
        addIfSet(attributter, "fom", arbeidsforhold.getFrom());
        addIfSet(attributter, "tom", arbeidsforhold.getTo());
        if (arbeidsforhold.getStillingsprosent() != null) {
            attributter.add(txt("stillingsprosent", prosentFra(arbeidsforhold.getStillingsprosent())));
        }
        return attributter;
    }

    float utenlandskeArbeidsforholdOpptjening(List<UtenlandskArbeidsforhold> utenlandskArbeidsforhold,
            List<Vedlegg> vedlegg, FontAwareCos cos,
            float y) throws IOException {
        if (CollectionUtils.isEmpty(utenlandskArbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("utenlandskarbeid"), cos, y);
        for (var forhold : sorterUtenlandske(utenlandskArbeidsforhold)) {
            y -= renderer.addLinesOfRegularText(utenlandskeArbeidsforhold(forhold), cos, y);
            y -= renderVedlegg(vedlegg, forhold.getVedlegg(), "vedleggutenlandskarbeid", cos, y);
        }
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private static List<UtenlandskArbeidsforhold> sorterUtenlandske(List<UtenlandskArbeidsforhold> arbeidsforhold) {
        Collections.sort(arbeidsforhold, (o1, o2) -> {
            if (o1.getPeriode() != null && o2.getPeriode() != null
                    && o1.getPeriode().fom() != null
                    && o2.getPeriode().fom() != null) {
                return o1.getPeriode().fom().compareTo(o2.getPeriode().fom());
            }
            return 0;
        });
        return arbeidsforhold;
    }

    private List<String> utenlandskeArbeidsforhold(UtenlandskArbeidsforhold ua) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "arbeidsgiver", ua.getArbeidsgiverNavn());
        addIfSet(attributter, "fom", ua.getPeriode().fom());
        addIfSet(attributter, "tom", ua.getPeriode().tom());
        addIfSet(attributter, "virksomhetsland", ua.getLand());
        return attributter;
    }

    private List<List<String>> egneNæringer(List<EgenNæring> egenNæring) {
        return safeStream(egenNæring)
                .map(this::egenNæring)
                .toList();
    }

    private List<String> egenNæring(EgenNæring næring) {
        List<String> attributter = new ArrayList<>();
        if (næring instanceof NorskOrganisasjon) {
            NorskOrganisasjon org = NorskOrganisasjon.class.cast(næring);
            addIfSet(attributter, "virksomhetsnavn", org.getOrgName());
            addIfSet(attributter, "orgnummer", org.getOrgNummer().value());
            addIfSet(attributter, "registrertiland", CountryCode.NO);
        }
        if (næring instanceof UtenlandskOrganisasjon) {
            UtenlandskOrganisasjon org = UtenlandskOrganisasjon.class.cast(næring);
            addIfSet(attributter, "virksomhetsnavn", org.getOrgName());
            addIfSet(attributter, "registrertiland", org.getRegistrertILand());
        }
        attributter.add(txt("egennæringtyper", næring.getVedlegg().size() > 1 ? "r" : "",
                safeStream(næring.getVirksomhetsTyper())
                        .map(v -> textFormatter.capitalize(v.toString()))
                        .collect(joining(","))));
        if (næring.getPeriode().tom() == null) {
            addIfSet(attributter, "egennæringpågår", textFormatter.dato(næring.getPeriode().fom()));
        } else {
            attributter.add(txt("egennæringavsluttet", næring.getPeriode().fom(),
                    textFormatter.dato(næring.getPeriode().tom())));
        }
        if (næring.getStillingsprosent() != null) {
            attributter.add(txt("stillingsprosent", prosentFra(næring.getStillingsprosent())));
        }
        attributter.add(txt("nyligyrkesaktiv", jaNei(næring.isErNyIArbeidslivet())));
        attributter.add(txt("varigendring", jaNei(næring.isErVarigEndring())));
        addIfSet(attributter, "egennæringbeskrivelseendring", næring.getBeskrivelseEndring());
        addIfSet(attributter, "egennæringendringsdato", næring.getEndringsDato());
        addMoneyIfSet(attributter, "egennæringbruttoinntekt", næring.getNæringsinntektBrutto());
        if (næring.isErNyOpprettet()) {
            attributter.add(txt("nystartetvirksomhet", jaNei(true)));
            addIfSet(attributter, "egennæringoppstartsdato", næring.getOppstartsDato());
        }
        Regnskapsfører rf = regnskapsfører(næring);
        if (rf != null) {
            if (rf.telefon() != null) {
                attributter.add(
                        txt("regnskapsførertelefon", rf.navn(), rf.telefon(), jaNei(næring.isNærRelasjon())));
            } else {
                attributter.add(txt("regnskapsfører", rf.navn(), jaNei(næring.isNærRelasjon())));
            }
        }
        return attributter;
    }

    private float renderVedlegg(List<Vedlegg> vedlegg, List<String> vedleggRefs, String keyIfAnnet, // Fra
                                                                                                    // svangerskapspenger
            FontAwareCos cos, float y) throws IOException {
        float startY = y;
        if (!vedleggRefs.isEmpty()) {
            y -= renderer.addBulletPoint(INDENT, txt("vedlegg1"), cos, y);
        }
        for (String vedleggRef : vedleggRefs) {
            Optional<Vedlegg> details = safeStream(vedlegg)
                    .peek(s -> LOG.debug("Sjekker vedlegg med id {} mot {}", s.getId(), vedleggRef)) // debug
                    .filter(s -> vedleggRef.equals(s.getId()))
                    .findFirst();
            if (details.isPresent()) {
                String beskrivelse = vedleggsBeskrivelse(keyIfAnnet, details.get());
                y -= renderer.addBulletPoint(INDENT,
                        txt("vedlegg2", beskrivelse, details.get().getInnsendingsType().name()),
                        cos, y);
            } else {
                // Never, hopefully
                y -= renderer.addBulletPoint(INDENT, txt("vedlegg2", "vedlegg"), cos, y);
            }
        }
        return startY - y;
    }

    private String vedleggsBeskrivelse(String key, Vedlegg vedlegg) {
        return erAnnenDokumentType(vedlegg) ? txt(key) : vedlegg.getBeskrivelse();
    }

    private static boolean erAnnenDokumentType(Vedlegg vedlegg) {
        return vedlegg.getDokumentType().equals(I000060) || vedlegg.getDokumentType().equals(I000049);
    }

    private static double prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::getProsent)
                .orElse(0d);
    }
}
