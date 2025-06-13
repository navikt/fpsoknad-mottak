package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.util.Comparator.comparing;
import static no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper.FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.pdf.PdfOutlineItem.FORELDREPENGER_OUTLINE;
import static no.nav.foreldrepenger.mottak.innsending.pdf.PdfThrowableFunction.uncheck;
import static no.nav.foreldrepenger.mottak.util.CollectionUtil.tryOrEmpty;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;
import no.nav.foreldrepenger.mottak.oversikt.Oversikt;
import no.nav.foreldrepenger.mottak.oversikt.EnkeltArbeidsforhold;

@Component
public class ForeldrepengerPdfGenerator implements MappablePdfGenerator {

    private static final float START_Y = PdfElementRenderer.calculateStartY();
    private final Oversikt arbeidsInfo;

    private final ForeldrepengeInfoRenderer fpRenderer;

    public ForeldrepengerPdfGenerator(Oversikt oversikt,
                                      ForeldrepengeInfoRenderer fpRenderer) {
        this.arbeidsInfo = oversikt;
        this.fpRenderer = fpRenderer;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return FORELDREPENGER;
    }

    @Override
    public byte[] generer(Søknad søknad, SøknadEgenskap egenskap, InnsendingPersonInfo person) {
        return switch (egenskap.getType()) {
            case INITIELL_FORELDREPENGER -> generer(søknad, person);
            case ENDRING_FORELDREPENGER -> generer((Endringssøknad) søknad, person);
            default -> throw new UnexpectedInputException("Ukjent type " + egenskap.getType() + " for søknad, kan ikke lage PDF");
        };
    }

    private byte[] generer(Søknad søknad, InnsendingPersonInfo person) {
        var foreldrepenger = (Foreldrepenger) søknad.getYtelse();

        try (var doc = new FontAwarePdfDocument(); var baos = new ByteArrayOutputStream()) {
            var page = new PDPage(A4);
            doc.addPage(page);
            fpRenderer.addOutlineItem(doc, page, FORELDREPENGER_OUTLINE);
            var cos = new FontAwareCos(doc, page);
            Function<CosyPair, Float> headerFn = uncheck(p -> fpRenderer.header(doc, p.cos(), false, p.y(), person));
            float y = headerFn.apply(new CosyPair(cos, START_Y));
            var docParam = new DocParam(doc, headerFn);
            var cosy = new CosyPair(cos, y);

            if (foreldrepenger.relasjonTilBarn() != null) {
                Function<CosyPair, Float> relasjonTilBarnFn = uncheck(p -> fpRenderer.relasjonTilBarn(foreldrepenger.relasjonTilBarn(),
                    søknad.getVedlegg(), p.cos, p.y));
                cosy = render(docParam, relasjonTilBarnFn, cosy);
            }

            var annenForelder = foreldrepenger.annenForelder();
            if (annenForelder != null) {
                Function<CosyPair, Float> annenForelderFn = uncheck(p -> fpRenderer.annenForelder(annenForelder, foreldrepenger.fordeling().erAnnenForelderInformert(),
                    foreldrepenger.rettigheter(), p.cos, p.y));
                cosy = render(docParam, annenForelderFn, cosy);
            }

            if (søknad.getTilleggsopplysninger() != null && !søknad.getTilleggsopplysninger().isBlank()) {
                Function<CosyPair, Float> tilleggsopplysningerFn = uncheck(p -> fpRenderer.renderTilleggsopplysninger(søknad.getTilleggsopplysninger(), p.cos, p.y));
                cosy = render(docParam, tilleggsopplysningerFn, cosy);
            }

            var opptjening = foreldrepenger.opptjening();
            var arbeidsforhold = aktiveArbeidsforhold(foreldrepenger.getFørsteUttaksdag());
            if (opptjening != null) {
                Function<CosyPair, Float> arbeidsforholdOpptjFn = uncheck(p -> fpRenderer.arbeidsforholdOpptjening(arbeidsforhold, p.cos, p.y));
                cosy = render(docParam, arbeidsforholdOpptjFn, cosy);

                if (!opptjening.utenlandskArbeidsforhold().isEmpty()) {
                    Function<CosyPair, Float> utenlandsArbeidsforholdFn = uncheck(p -> fpRenderer.utenlandskeArbeidsforholdOpptjening(
                        opptjening.utenlandskArbeidsforhold(),
                        søknad.getVedlegg(), p.cos, p.y));
                    cosy = render(docParam, utenlandsArbeidsforholdFn, cosy);
                }

                if (!opptjening.annenOpptjening().isEmpty()) {
                    Function<CosyPair, Float> annenOpptjeningFn = uncheck(p -> fpRenderer.annenOpptjening(opptjening.annenOpptjening(),
                        søknad.getVedlegg(), p.cos, p.y));
                    cosy = render(docParam, annenOpptjeningFn, cosy);
                }

                if (!opptjening.egenNæring().isEmpty()) {
                    Function<CosyPair, Float> egenNæringFn = uncheck(p ->
                        fpRenderer.egneNæringerOpptjening(opptjening.egenNæring(), p.cos, p.y));
                    cosy = render(docParam, egenNæringFn, cosy);
                }

                if (opptjening.frilans() != null) {
                    Function<CosyPair, Float> frilansFn = uncheck(p ->
                        fpRenderer.frilansOpptjening(opptjening.frilans(), p.cos, p.y));
                    cosy = render(docParam, frilansFn, cosy);
                }

                if (foreldrepenger.utenlandsopphold() != null) {
                    Function<CosyPair, Float> medlemskapFn = uncheck(p ->
                        fpRenderer.utenlandsopphold(foreldrepenger.utenlandsopphold(), p.cos, p.y));
                    cosy = render(docParam, medlemskapFn, cosy);
                }

                if (foreldrepenger.fordeling() != null) {
                    var forCos = fpRenderer.fordeling(doc, søknad.getSøker().søknadsRolle(), foreldrepenger,
                            søknad.getVedlegg(), false, cosy.cos(), cosy.y(), person);
                    cosy = new CosyPair(forCos, -1);
                }

            }
            cosy.cos().close();
            doc.save(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new UnexpectedInputException("Kunne ikke lage PDF", e);
        }
    }

    private byte[] generer(Endringssøknad søknad, InnsendingPersonInfo person) {
        var ytelse = (Foreldrepenger) søknad.getYtelse();

        try (var doc = new FontAwarePdfDocument(); var baos = new ByteArrayOutputStream()) {
            var page = new PDPage(A4);
            doc.addPage(page);
            Function<CosyPair, Float> headerFn = uncheck(p -> fpRenderer.header(doc, p.cos(), true, p.y(), person));
            var docParam = new DocParam(doc, headerFn);
            var cosy = new CosyPair(new FontAwareCos(doc, page), START_Y);
            cosy = new CosyPair(cosy.cos(), headerFn.apply(cosy));

            if (ytelse.relasjonTilBarn() != null) {
                Function<CosyPair, Float> relasjonTilBarnFn = uncheck(p -> fpRenderer.relasjonTilBarn(ytelse.relasjonTilBarn(),
                    søknad.getVedlegg(), p.cos, p.y));
                cosy = render(docParam, relasjonTilBarnFn, cosy);
            }

            var annenForelder = ytelse.annenForelder();
            if (annenForelder != null) {
                Function<CosyPair, Float> annenForelderFn = uncheck(p -> fpRenderer.annenForelder(annenForelder, ytelse.fordeling().erAnnenForelderInformert(),
                    ytelse.rettigheter(), p.cos, p.y));
                cosy = render(docParam, annenForelderFn, cosy);
            }

            var tilleggsopplysninger = søknad.getTilleggsopplysninger();
            if (tilleggsopplysninger != null && !tilleggsopplysninger.isBlank()) {
                Function<CosyPair, Float> tilleggsopplysningerFn = uncheck(p -> fpRenderer.renderTilleggsopplysninger(søknad.getTilleggsopplysninger(), p.cos, p.y));
                cosy = render(docParam, tilleggsopplysningerFn, cosy);
            }

            if (ytelse.fordeling() != null) {
                var fordelCos = fpRenderer.fordeling(doc, søknad.getSøker().søknadsRolle(), ytelse,
                    søknad.getVedlegg(), true, cosy.cos(), cosy.y(), person);
                cosy = new CosyPair(fordelCos, -1);
            }
            cosy.cos().close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new UnexpectedInputException("Kunne ikke lage PDF", e);
        }
    }

    private List<EnkeltArbeidsforhold> aktiveArbeidsforhold(LocalDate førsteUttaksdato) {
        return tryOrEmpty(arbeidsInfo::hentArbeidsforhold).stream()
            .filter(a -> a.to().isEmpty() ||  a.to().get().isAfter(førsteUttaksdato))
            .sorted(comparing(EnkeltArbeidsforhold::from))
            .toList();
    }

    private static CosyPair render(DocParam param, Function<CosyPair, Float> renderFunction, CosyPair cosy) throws IOException {
        var utkastPage = new PDPage(A4);
        var utkastCos = new FontAwareCos(param.doc(), utkastPage);
        var startYEtterHeader = param.headerFn().apply(new CosyPair(utkastCos, START_Y));
        var utkastCosy = new CosyPair(utkastCos, startYEtterHeader);
        var utkastY = renderFunction.apply(utkastCosy);
        var fitsAvailableYSpace = startYEtterHeader - utkastY - PdfElementRenderer.MARGIN <= cosy.y();
        if (fitsAvailableYSpace) {
            // innholdet passer på eksisterende side, da skriver vi til den
            utkastCos.close();
            return new CosyPair(cosy.cos(), renderFunction.apply(cosy));
        } else {
            cosy.cos().close();
            param.doc().addPage(utkastPage);
            return new CosyPair(utkastCos, utkastY);
        }
    }

    private record CosyPair(FontAwareCos cos, float y) { }

    private record DocParam(FontAwarePdfDocument doc, Function<CosyPair, Float> headerFn) { }

}
