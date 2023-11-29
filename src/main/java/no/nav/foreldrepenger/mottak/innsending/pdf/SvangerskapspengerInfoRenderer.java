package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.common.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.common.domain.felles.InnsendingsType.SEND_SENERE;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.VedleggMetaData;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Frilanser;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@Component
public class SvangerskapspengerInfoRenderer extends FellesSøknadInfoRenderer {

    public SvangerskapspengerInfoRenderer(PdfElementRenderer renderer, SøknadTextFormatter textFormatter) {
        super(renderer, textFormatter);
    }

    public float vedleggSomErOpplastet(List<Vedlegg> vedlegg, List<EnkeltArbeidsforhold> arbeidsgivere, FontAwareCos cos, float y) throws IOException {
        if (CollectionUtils.isEmpty(vedlegg)) {
            return y;
        }
        var opplastedeVedlegg = vedlegg.stream()
            .filter(v -> LASTET_OPP.equals(v.getInnsendingsType()))
            .toList();
        if (!opplastedeVedlegg.isEmpty()) {
            y -= renderer.addLeftHeading(txt("dokumentasjon.mottatt.overskrift"), cos, y);
            y = vedlegg(opplastedeVedlegg, arbeidsgivere, cos, y);
        }
        return y;
    }

    public float vedleggSomEttersendes(List<Vedlegg> vedlegg, List<EnkeltArbeidsforhold> arbeidsgivere, FontAwareCos cos, float y) throws IOException {
        if (CollectionUtils.isEmpty(vedlegg)) {
            return y;
        }
        var sendSenereVedlegg = vedlegg.stream()
            .filter(v -> SEND_SENERE.equals(v.getInnsendingsType()))
            .toList();
        if (!sendSenereVedlegg.isEmpty()) {
            y -= renderer.addLeftHeading(txt("dokumentasjon.senere.overskrift"), cos, y);
            y = vedlegg(sendSenereVedlegg, arbeidsgivere, cos, y);
            y -= renderer.addLineOfRegularText(txt("dokumentasjon.innsyn"), cos, y);
        }
        return y;
    }


    private float vedlegg(List<Vedlegg> vedleggene, List<EnkeltArbeidsforhold> arbeidsgivere, FontAwareCos cos, float y) throws IOException {
        for (var vedlegg : vedleggene) {
            var dokumentInnslag = new StringBuilder(tilDokumentBeskrivelse(vedlegg.getDokumentType()));

            var hvaDokumentererVedlegg = vedlegg.getMetadata().hvaDokumentererVedlegg();
            if (hvaDokumentererVedlegg != null && hvaDokumentererVedlegg.type().equals(VedleggMetaData.Dokumenterer.Type.TILRETTELEGGING)) {
                var navnPåArbeidsgiver = navnArbeidsgiver(arbeidsgivere, hvaDokumentererVedlegg);
                dokumentInnslag.append(" for ").append(navnPåArbeidsgiver);
            }

            if (vedlegg.getInnsendingsType().equals(LASTET_OPP) && vedlegg.getMetadata().filnavn() != null && !vedlegg.getMetadata().filnavn().isBlank()) {
                dokumentInnslag
                    .append(" (")
                    .append(vedlegg.getMetadata().filnavn())
                    .append(")");
            }

            y -= renderer.addBulletPoint(INDENT, dokumentInnslag.toString(), cos, y);
        }
        y -= PdfElementRenderer.BLANK_LINE;

        return y;
    }

    private String navnArbeidsgiver(List<EnkeltArbeidsforhold> arbeidsgivere, VedleggMetaData.Dokumenterer hvaDokumentererVedlegg) {
        var arbeidsforhold = hvaDokumentererVedlegg.arbeidsforhold();
        return switch (arbeidsforhold) {
            case Virksomhet v -> virksomhetsnavn(arbeidsgivere, v.orgnr().value()).orElse(txt("arbeidsgiverIkkeFunnet", v.orgnr().value()));
            case PrivatArbeidsgiver p -> virksomhetsnavn(arbeidsgivere, p.fnr().value()).orElse(txt("svp.privatarbeidsgiverNavnIkkeFunnet"));
            case Frilanser f -> txt("svp.frilans");
            case SelvstendigNæringsdrivende s -> txt("svp.selvstendig");
        };
    }

    public Optional<String> virksomhetsnavn(List<EnkeltArbeidsforhold> arbeidsgivere, String arbeidsgiverId) {
        return safeStream(arbeidsgivere)
            .filter(arb -> arb.arbeidsgiverId().equals(arbeidsgiverId))
            .findFirst()
            .map(EnkeltArbeidsforhold::arbeidsgiverNavn);
    }
}
