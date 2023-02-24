package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper.FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.pdf.PdfOutlineItem.FORELDREPENGER_OUTLINE;
import static no.nav.foreldrepenger.mottak.innsending.pdf.PdfThrowableFunction.uncheck;
import static no.nav.foreldrepenger.mottak.util.CollectionUtil.tryOrEmpty;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

@Component
public class ForeldrepengerPdfGenerator implements MappablePdfGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerPdfGenerator.class);
    private static final float INITIAL_Y = PdfElementRenderer.calculateStartY();
    private final Oppslag oppslag;
    private final ArbeidsInfo arbeidsforhold;

    private final ForeldrepengeInfoRenderer fpRenderer;
    private final InfoskrivRenderer infoskrivRenderer;

    public ForeldrepengerPdfGenerator(Oppslag oppslag, ArbeidsInfo arbeidsforhold,
            ForeldrepengeInfoRenderer fpRenderer,
            InfoskrivRenderer infoskrivRenderer) {
        this.oppslag = oppslag;
        this.arbeidsforhold = arbeidsforhold;
        this.fpRenderer = fpRenderer;
        this.infoskrivRenderer = infoskrivRenderer;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return FORELDREPENGER;
    }

    @Override
    public byte[] generer(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return switch (egenskap.getType()) {
            case INITIELL_FORELDREPENGER -> generer(søknad, søker);
            case ENDRING_FORELDREPENGER -> generer((Endringssøknad) søknad, søker);
            default -> throw new UnexpectedInputException("Ukjent type " + egenskap.getType() + " for søknad, kan ikke lage PDF");
        };
    }

    private byte[] generer(Søknad søknad, Person søker) {
        var stønad = (Foreldrepenger) søknad.getYtelse();

        try (var doc = new FontAwarePdfDocument(); var baos = new ByteArrayOutputStream()) {
            var page = new PDPage(A4);
            doc.addPage(page);
            fpRenderer.addOutlineItem(doc, page, FORELDREPENGER_OUTLINE);
            var cos = new FontAwareCos(doc, page);
            Function<CosyPair, Float> headerFn = uncheck(p -> fpRenderer.header(søker, doc, p.cos(), false, p.y()));
            float y = headerFn.apply(new CosyPair(cos, INITIAL_Y));
            var docParam = new DocParam(doc, søker, headerFn);
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

            Opptjening opptjening = stønad.opptjening();
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

                if (stønad.medlemsskap() != null) {
                    Function<CosyPair, Float> medlemskapFn = uncheck(p ->
                        fpRenderer.medlemsskap(stønad.medlemsskap(), stønad.relasjonTilBarn(), p.cos, p.y));
                    cosy = render(docParam, medlemskapFn, cosy);
                }

                if (stønad.fordeling() != null) {
                    var forCos = fpRenderer.fordeling(doc, søker, søknad.getSøker().søknadsRolle(), stønad,
                            søknad.getVedlegg(), false, cosy.cos(), cosy.y());
                    cosy = new CosyPair(forCos, -1);
                }

                if (!arbeidsforhold.isEmpty()) {
                    cosy = new CosyPair(infoskrivRenderer.renderInfoskriv(arbeidsforhold, søker, søknad, cosy.cos(), doc), -1);
                }
            }
            cosy.cos().close();
            doc.save(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            LOG.warn("Kunne ikke lage PDF", e);
            throw new UnexpectedInputException("Kunne ikke lage PDF", e);
        }
    }

    private byte[] generer(Endringssøknad søknad, Person søker) {
        var ytelse = (Foreldrepenger) søknad.getYtelse();

        try (var doc = new FontAwarePdfDocument(); var baos = new ByteArrayOutputStream()) {
            var page = new PDPage(A4);
            doc.addPage(page);
            Function<CosyPair, Float> headerFn = uncheck(p -> fpRenderer.header(søker, doc, p.cos(), true, p.y()));
            var docParam = new DocParam(doc, søker, headerFn);
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
                var fordelCos = fpRenderer.fordeling(doc, søker, søknad.getSøker().søknadsRolle(), ytelse,
                    søknad.getVedlegg(), true, cosy.cos(), cosy.y());
                cosy = new CosyPair(fordelCos, -1);
            }
            cosy.cos().close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            LOG.warn("Kunne ikke lage PDF", e);
            throw new UnexpectedInputException("Kunne ikke lage PDF", e);
        }
    }

    private List<EnkeltArbeidsforhold> aktiveArbeidsforhold(LocalDate relasjonsdato) {
        return tryOrEmpty(arbeidsforhold::hentArbeidsforhold).stream()
            .filter(a -> a.to().isEmpty() || (a.to().isPresent() && a.to().get().isAfter(relasjonsdato)))
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", fpRenderer=" + fpRenderer
            + ", mapperEgenskaper=" + mapperEgenskaper() + "]";
    }

    private record CosyPair(FontAwareCos cos, float y) { }

    private record DocParam(FontAwarePdfDocument doc, Person søker, Function<CosyPair, Float> headerFn) { }

}
