package no.nav.foreldrepenger.mottak.innsending.pdf;


import com.google.common.base.Joiner;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.*;
import no.nav.foreldrepenger.mottak.domain.felles.ÅpenPeriode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.neovisionaries.i18n.CountryCode;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000060;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

@Component
public class SvangerskapspengerInfoRenderer {
    private static final float STARTY = PDFElementRenderer.calculateStartY();
    private static final int INDENT = 20;
    private final PDFElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    public SvangerskapspengerInfoRenderer(PDFElementRenderer renderer, SøknadTextFormatter textFormatter) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
    }

    public float frilansOpptjening(Frilans frilans, FontAwareCos cos, float y) throws IOException {
        if (frilans == null) {
            return y;
        }
        y = frilans(frilans, cos, y);
        return y;
    }

    private float frilans(Frilans frilans, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("frilans"), cos, y);
        List<String> attributter = new ArrayList<>();
        if (frilans.getPeriode().getTom() == null) {
            addIfSet(attributter, "frilanspågår", textFormatter.dato(frilans.getPeriode().getFom()));
        } else {
            attributter.add(txt("frilansavsluttet", textFormatter.dato(frilans.getPeriode().getFom()),
                textFormatter.dato(frilans.getPeriode().getTom())));
        }
        attributter.add(txt("fosterhjem", jaNei(frilans.isHarInntektFraFosterhjem())));
        attributter.add(txt("nyoppstartet", jaNei(frilans.isNyOppstartet())));

        y -= renderer.addLinesOfRegularText(attributter, cos, y);
        if (!frilans.getFrilansOppdrag().isEmpty()) {
            y -= renderer.addLineOfRegularText(txt("oppdrag"), cos, y);
            List<String> oppdrag = safeStream(frilans.getFrilansOppdrag())
                .map(o -> o.getOppdragsgiver() + " " + textFormatter.periode(o.getPeriode()))
                .collect(toList());
            y -= renderer.addBulletList(INDENT, oppdrag, cos, y);
            y -= renderer.addBlankLine();
        } else {
            y -= renderer.addLineOfRegularText(txt("oppdrag") + ": Nei", cos, y);
        }
        y -= renderer.addBlankLine();
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

    private void addListIfSet(List<String> attributter, String key, List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        addIfSet(attributter, key, Joiner.on(",").join(values));
    }

    private void addIfSet(List<String> attributter, boolean value, String key, String otherValue) {
        if (value) {
            attributter.add(txt(key, otherValue));
        }
    }

    private void addIfSet(List<String> attributter, String key, LocalDate dato) {
        if (dato != null) {
            attributter.add(txt(key, textFormatter.dato(dato)));
        }
    }

    private void addIfSet(List<String> attributter, String key, List<LocalDate> datoer) {
        if (!CollectionUtils.isEmpty(datoer)) {
            attributter.add(txt(key, textFormatter.datoer(datoer)));
        }
    }

    private void addIfSet(List<String> attributter, String key, Optional<LocalDate> dato) {
        if (dato.isPresent()) {
            attributter.add(txt(key, textFormatter.dato(dato.get())));
        }
    }

    private void addIfSet(List<String> attributter, ÅpenPeriode periode) {
        if (periode != null) {
            addIfSet(attributter, "fom", periode.getFom());
            addIfSet(attributter, "tom", periode.getTom());
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

    public float egneNæringerOpptjening(List<EgenNæring> egneNæringer, FontAwareCos cos, float y)
        throws IOException {
        if (CollectionUtils.isEmpty(egneNæringer)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("egennæring"), cos, y);
        for (List<String> næring : egneNæringer(egneNæringer)) {
            y -= renderer.addLinesOfRegularText(næring, cos, y);
            y -= renderer.addBlankLine();
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
        for (UtenlandskArbeidsforhold forhold : sorterUtenlandske(utenlandskArbeidsforhold)) {
            y -= renderer.addLinesOfRegularText(utenlandskeArbeidsforhold(forhold), cos, y);
            y -= renderVedlegg(vedlegg, forhold.getVedlegg(), "vedleggutenlandskarbeid", cos, y);
        }
        y -= renderer.addBlankLine();
        return y;
    }

    private static List<UtenlandskArbeidsforhold> sorterUtenlandske(List<UtenlandskArbeidsforhold> arbeidsforhold) {
        Collections.sort(arbeidsforhold, (o1, o2) -> {
            if (o1.getPeriode() != null && o2.getPeriode() != null
                && o1.getPeriode().getFom() != null
                && o2.getPeriode().getFom() != null) {
                return o1.getPeriode().getFom().compareTo(o2.getPeriode().getFom());
            }
            return 0;
        });
        return arbeidsforhold;
    }

    private List<String> utenlandskeArbeidsforhold(UtenlandskArbeidsforhold ua) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "arbeidsgiver", ua.getArbeidsgiverNavn());
        addIfSet(attributter, "fom", ua.getPeriode().getFom());
        addIfSet(attributter, "tom", ua.getPeriode().getTom());
        addIfSet(attributter, "virksomhetsland", ua.getLand());
        return attributter;
    }

    private List<List<String>> egneNæringer(List<EgenNæring> egenNæring) {
        return safeStream(egenNæring)
            .map(this::egenNæring)
            .collect(toList());
    }

    private List<String> egenNæring(EgenNæring næring) {
        List<String> attributter = new ArrayList<>();
        if (næring instanceof NorskOrganisasjon) {
            NorskOrganisasjon org = NorskOrganisasjon.class.cast(næring);
            addIfSet(attributter, "virksomhetsnavn", org.getOrgName());
            addIfSet(attributter, "orgnummer", org.getOrgNummer());
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
        if (næring.getPeriode().getTom() == null) {
            addIfSet(attributter, "egennæringpågår", textFormatter.dato(næring.getPeriode().getFom()));
        } else {
            attributter.add(txt("egennæringavsluttet", næring.getPeriode().getFom(),
                textFormatter.dato(næring.getPeriode().getTom())));
        }
        if (næring.getStillingsprosent() != null) {
            attributter.add(txt("stillingsprosent", prosentFra(næring.getStillingsprosent())));
        }
        attributter.add(txt("nyopprettet", jaNei(næring.isErNyOpprettet())));
        attributter.add(txt("varigendring", jaNei(næring.isErVarigEndring())));
        addIfSet(attributter, "egennæringbeskrivelseendring", næring.getBeskrivelseEndring());
        addIfSet(attributter, "egennæringendringsdato", næring.getEndringsDato());
        addMoneyIfSet(attributter, "egennæringbruttoinntekt", næring.getNæringsinntektBrutto());
        if (næring.isErNyIArbeidslivet()) {
            attributter.add(txt("nyiarbeidslivet", jaNei(true)));
            addIfSet(attributter, "egennæringoppstartsdato", næring.getOppstartsDato());
        }
        Regnskapsfører rf = regnskapsfører(næring);
        if (rf != null) {
            if (rf.getTelefon() != null) {
                attributter.add(
                    txt("regnskapsførertelefon", rf.getNavn(), rf.getTelefon(), jaNei(næring.isNærRelasjon())));
            } else {
                attributter.add(txt("regnskapsfører", rf.getNavn(), jaNei(næring.isNærRelasjon())));
            }
        }

        return attributter;
    }

    private float renderVedlegg(List<Vedlegg> vedlegg, List<String> vedleggRefs, String keyIfAnnet, // Fra svangerskapspenger
                                FontAwareCos cos, float y) throws IOException {
        float startY = y;
        if (!vedleggRefs.isEmpty()) {
            y -= renderer.addBulletPoint(INDENT, txt("vedlegg1"), cos, y);
        }
        for (String vedleggRef : vedleggRefs) {
            Optional<Vedlegg> details = safeStream(vedlegg)
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
        return vedlegg.getDokumentType().equals(I000060);
    }

    private static double prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
            .map(ProsentAndel::getProsent)
            .orElse(0d);
    }

}
