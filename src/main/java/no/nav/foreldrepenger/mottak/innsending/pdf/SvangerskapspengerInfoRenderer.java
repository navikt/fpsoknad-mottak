package no.nav.foreldrepenger.mottak.innsending.pdf;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.felles.ÅpenPeriode;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.AvtaltFerie;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.arbeidsforhold.PrivatArbeidsgiver;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.arbeidsforhold.Virksomhet;
import no.nav.foreldrepenger.mottak.oversikt.EnkeltArbeidsforhold;

import static no.nav.foreldrepenger.mottak.innsending.pdf.SvangerskapspengerHelper.virksomhetsnavn;

@Component
public class SvangerskapspengerInfoRenderer extends FellesSøknadInfoRenderer {

    private static final int INDENT = 20;
    private final PdfElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    public SvangerskapspengerInfoRenderer(PdfElementRenderer renderer, SøknadTextFormatter textFormatter) {
        super(renderer, textFormatter);
        this.renderer = renderer;
        this.textFormatter = textFormatter;
    }

    protected float feriePerioder(List<EnkeltArbeidsforhold> aktiveArbeidsforhold, List<AvtaltFerie> avtaltFerie, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("svp.ferie.overskrift"), cos, y);
        var formattertPlanlagtFerie = txt("svp.ferie.planlagt.ferie", textFormatter.yesNo(!avtaltFerie.isEmpty()));
        y -= renderer.addLineOfRegularText(INDENT, formattertPlanlagtFerie, cos, y);
        var visArbeidsgiverNavn = aktiveArbeidsforhold.size() > 1;
        var avtaltFeriePerArbeidsgiver = avtaltFerie.stream().collect(Collectors.groupingBy(AvtaltFerie::arbeidsforhold));
        for (var virksomhet : avtaltFeriePerArbeidsgiver.entrySet()) {
            y -= PdfElementRenderer.BLANK_LINE;
            var arbeidsgiverNavn = arbeidsgiverNavn(aktiveArbeidsforhold, virksomhet.getKey());
            y -= renderFerie(arbeidsgiverNavn, virksomhet.getValue(), visArbeidsgiverNavn, cos, y);
        }
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private String arbeidsgiverNavn(List<EnkeltArbeidsforhold> arbeidsgivere, Arbeidsforhold arbeidsforhold) {
        return switch (arbeidsforhold) {
            case Virksomhet(Orgnummer orgnummer) -> {
                var virksomhetNavn = virksomhetsnavn(arbeidsgivere, orgnummer.value());
                yield virksomhetNavn
                    .map(navn -> txt("virksomhetsnavn", navn))
                    .orElseGet(() -> txt("orgnummer", orgnummer.value()));
            }
            case PrivatArbeidsgiver(Fødselsnummer fnr) -> {
                var personnavnArbeidsgiver = virksomhetsnavn(arbeidsgivere, fnr.value());
                yield personnavnArbeidsgiver.orElse(txt("svp.privatarbeidsgiverNavnIkkeFunnet"));
            }

            default -> throw new IllegalArgumentException("Støtter ikke arbeidsforhold av typen " + arbeidsgivere.getClass().getSimpleName());
        };
    }

    private float renderFerie(String arbeidsgiverNavn,
                              List<AvtaltFerie> avtaltFerie,
                              boolean visOverskrift,
                              FontAwareCos cos,
                              float y) throws IOException {
        var startY = y;
        if (visOverskrift) {
            y -= renderer.addLineOfRegularText(INDENT, arbeidsgiverNavn, cos, y);
        }
        y -= renderer.addLineOfRegularText(INDENT, txt("svp.ferie.perioder"), cos, y);
        y -= renderDatoPerioder(avtaltFerie, cos, y);
        return startY - y;
    }

    private float renderDatoPerioder(List<AvtaltFerie> ferie, FontAwareCos cos, float y) throws IOException {
        var startY = y;
        for (var feriePeriode : ferie) {
            var generellPeriode = new ÅpenPeriode(feriePeriode.ferieFom(), feriePeriode.ferieTom());
            var formattertPeriode = textFormatter.enkelPeriode(generellPeriode);
            y -= renderer.addBulletPoint(INDENT * 2, formattertPeriode, cos, y);
        }
        return startY - y;
    }

}
