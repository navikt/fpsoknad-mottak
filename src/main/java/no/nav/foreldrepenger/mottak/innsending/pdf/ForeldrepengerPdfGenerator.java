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
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@Component
public class ForeldrepengerPdfGenerator implements MappablePdfGenerator {

    private static final float INITIAL_Y = PdfElementRenderer.calculateStartY();
    private final ArbeidsInfo arbeidsInfo;

    private final ForeldrepengeInfoRenderer fpRenderer;
    private final InfoskrivRenderer infoskrivRenderer;

    public ForeldrepengerPdfGenerator(ArbeidsInfo arbeidsInfo,
                                      ForeldrepengeInfoRenderer fpRenderer,
                                      InfoskrivRenderer infoskrivRenderer) {
        this.arbeidsInfo = arbeidsInfo;
        this.fpRenderer = fpRenderer;
        this.infoskrivRenderer = infoskrivRenderer;
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
        var stønad = (Foreldrepenger) søknad.getYtelse();

        try (var doc = new FontAwarePdfDocument(); var baos = new ByteArrayOutputStream()) {
            var page = new PDPage(A4);
            doc.addPage(page);
            fpRenderer.addOutlineItem(doc, page, FORELDREPENGER_OUTLINE);
            var cos = new FontAwareCos(doc, page);
            Function<CosyPair, Float> headerFn = uncheck(p -> fpRenderer.header(doc, p.cos(), false, p.y(), person));
            float y = headerFn.apply(new CosyPair(cos, INITIAL_Y));
            var docParam = new DocParam(doc, headerFn);
            var cosy = new CosyPair(cos, y);

            if (stønad.relasjonTilBarn() != null) {
                Function<CosyPair, Float> relasjonTilBarnFn = uncheck(p -> fpRenderer.relasjonTilBarn(stønad.relasjonTilBarn(),
                    søknad.getVedlegg(), p.cos, p.y));
                cosy = render(docParam, relasjonTilBarnFn, cosy);
            }

            var annenForelder = stønad.annenForelder();
            if (annenForelder != null) {
                Function<CosyPair, Float> annenForelderFn = uncheck(p -> fpRenderer.annenForelder(annenForelder, stønad.fordeling().erAnnenForelderInformert(),
                    stønad.rettigheter(), p.cos, p.y));
                cosy = render(docParam, annenForelderFn, cosy);
            }

            if (søknad.getTilleggsopplysninger() != null && !søknad.getTilleggsopplysninger().isBlank()) {
                Function<CosyPair, Float> tilleggsopplysningerFn = uncheck(p -> fpRenderer.renderTilleggsopplysninger(søknad.getTilleggsopplysninger(), p.cos, p.y));
                cosy = render(docParam, tilleggsopplysningerFn, cosy);
            }

            var opptjening = stønad.opptjening();
            var arbeidsforhold = aktiveArbeidsforhold(stønad.relasjonTilBarn().relasjonsDato());
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

                if (stønad.utenlandsopphold() != null) {
                    Function<CosyPair, Float> medlemskapFn = uncheck(p ->
                        fpRenderer.utenlandsopphold(stønad.utenlandsopphold(), p.cos, p.y));
                    cosy = render(docParam, medlemskapFn, cosy);
                }

                if (stønad.fordeling() != null) {
                    var forCos = fpRenderer.fordeling(doc, søknad.getSøker().søknadsRolle(), stønad,
                            søknad.getVedlegg(), false, cosy.cos(), cosy.y(), person);
                    cosy = new CosyPair(forCos, -1);
                }

                if (!arbeidsforhold.isEmpty()) {
                    cosy = new CosyPair(infoskrivRenderer.renderInfoskriv(arbeidsforhold, søknad, cosy.cos(), doc, person), -1);
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
            var cosy = new CosyPair(new FontAwareCos(doc, page), INITIAL_Y);
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

    private List<EnkeltArbeidsforhold> aktiveArbeidsforhold(LocalDate relasjonsdato) {
        return tryOrEmpty(arbeidsInfo::hentArbeidsforhold).stream()
            .filter(a -> a.to().isEmpty() || a.to().get().isAfter(relasjonsdato))
            .sorted(comparing(EnkeltArbeidsforhold::from))
            .toList();
    }

    private static CosyPair render(DocParam param, Function<CosyPair, Float> renderFunction, CosyPair cosy) throws IOException {
        var scratchPage = new PDPage(A4);
        var scratchCos = new FontAwareCos(param.doc(), scratchPage);
        var initialYAfterHeader = param.headerFn().apply(new CosyPair(scratchCos, INITIAL_Y));
        var scratchCosy = new CosyPair(scratchCos, initialYAfterHeader);
        var scratchY = renderFunction.apply(scratchCosy);
        var fitsAvailableYSpace = initialYAfterHeader - scratchY <= cosy.y();
        if (fitsAvailableYSpace) {
            // innholdet passer på eksisterende side, da skriver vi til den
            scratchCos.close();
            return new CosyPair(cosy.cos(), renderFunction.apply(cosy));
        } else {
            cosy.cos().close();
            param.doc().addPage(scratchPage);
            return new CosyPair(scratchCos, scratchY);
        }
    }

    private record CosyPair(FontAwareCos cos, float y) { }

    private record DocParam(FontAwarePdfDocument doc, Function<CosyPair, Float> headerFn) { }

}
