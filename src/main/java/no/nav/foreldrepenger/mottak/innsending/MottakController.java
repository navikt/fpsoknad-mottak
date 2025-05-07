package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.common.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.common.innsending.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.innsending.SøknadValidator.validerFørstegangssøknad;
import static no.nav.foreldrepenger.mottak.innsending.SøknadValidator.validerSøknad;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.felles.EttersendingsType;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;
import no.nav.foreldrepenger.mottak.oversikt.Oversikt;
import no.nav.foreldrepenger.mottak.oversikt.OversiktTjeneste;
import no.nav.foreldrepenger.mottak.oversikt.PersonDto;

@ProtectedRestController(MottakController.INNSENDING)
public class MottakController {
    private static final Logger LOG = LoggerFactory.getLogger(MottakController.class);

    public static final String INNSENDING = "/mottak";
    private static final String VEDLEGG_REFERANSE_HEADER = "vedleggsreferanse";
    private static final String BODY_PART_NAME = "body";
    private static final String VEDLEGG_PART_NAME = "vedlegg";

    private final Oversikt oversikt;
    private final SøknadSender søknadSender;

    public MottakController(SøknadSender søknadSender,
                            OversiktTjeneste oversikt) {
        this.søknadSender = søknadSender;
        this.oversikt = oversikt;
    }

    @PostMapping(value = "/send", consumes = { MediaType.MULTIPART_MIXED_VALUE })
    public Kvittering sendSøknad(@Valid @RequestPart(BODY_PART_NAME) Søknad søknad,
                                 @Valid @RequestPart(value = VEDLEGG_PART_NAME, required = false) List<@Valid Part> vedlegg) {
        var søknadEgenskap = Inspektør.inspiser(søknad);
        hentInnholdOpplastedeVedlegg(søknad.getVedlegg(), vedlegg);
        validerRiktigAntallVedlegg(søknad.getVedlegg(), vedlegg);
        validerFørstegangssøknad(søknad);
        var innsendingPersonInfo = personInfo(tilYtelse(søknad.getYtelse()));
        return søknadSender.søk(søknad, søknadEgenskap, innsendingPersonInfo);
    }

    @PostMapping(value = "/endre", consumes = { MediaType.MULTIPART_MIXED_VALUE })
    public Kvittering endre(@Valid @RequestPart(BODY_PART_NAME) Endringssøknad endringssøknad,
                            @Valid @RequestPart(value = VEDLEGG_PART_NAME, required = false) List<@Valid Part> vedlegg) {
        hentInnholdOpplastedeVedlegg(endringssøknad.getVedlegg(), vedlegg);
        validerRiktigAntallVedlegg(endringssøknad.getVedlegg(), vedlegg);
        validerSøknad(endringssøknad.getYtelse());
        var innsendingPersonInfo = personInfo(tilYtelse(endringssøknad.getYtelse()));
        return søknadSender.endreSøknad(endringssøknad, ENDRING_FORELDREPENGER, innsendingPersonInfo);
    }

    @PostMapping(value = "/ettersend", consumes = { MediaType.MULTIPART_MIXED_VALUE })
    public Kvittering ettersend(@Valid @RequestPart(BODY_PART_NAME) Ettersending ettersending,
                                @Valid @RequestPart(value = VEDLEGG_PART_NAME, required = false) List<@Valid Part> vedlegg) {
        hentInnholdOpplastedeVedlegg(ettersending.vedlegg(), vedlegg);
        validerRiktigAntallVedlegg(ettersending.vedlegg(), vedlegg);
        var innsendingPersonInfo = personInfo(tilYtelse(ettersending.type()));
        return søknadSender.ettersend(ettersending, Inspektør.inspiser(ettersending), innsendingPersonInfo);
    }



    private InnsendingPersonInfo personInfo(no.nav.foreldrepenger.mottak.oversikt.Ytelse ytelse) {
        var person = oversikt.personinfo(ytelse);
        return map(person);
    }

    private static InnsendingPersonInfo map(PersonDto person) {
        return new InnsendingPersonInfo(person.navn(), person.aktørid(), person.fnr());
    }

    private static no.nav.foreldrepenger.mottak.oversikt.Ytelse tilYtelse(EttersendingsType ettersendingsType) {
        return switch (ettersendingsType) {
            case ENGANGSSTØNAD -> no.nav.foreldrepenger.mottak.oversikt.Ytelse.ENGANGSSTONAD;
            case FORELDREPENGER -> no.nav.foreldrepenger.mottak.oversikt.Ytelse.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> no.nav.foreldrepenger.mottak.oversikt.Ytelse.SVANGERSKAPSPENGER;
        };
    }
    private static no.nav.foreldrepenger.mottak.oversikt.Ytelse tilYtelse(no.nav.foreldrepenger.common.domain.Ytelse ytelse) {
        if (ytelse instanceof Engangsstønad) {
            return no.nav.foreldrepenger.mottak.oversikt.Ytelse.ENGANGSSTONAD;
        } else if (ytelse instanceof Svangerskapspenger) {
            return no.nav.foreldrepenger.mottak.oversikt.Ytelse.SVANGERSKAPSPENGER;
        } else {
            return no.nav.foreldrepenger.mottak.oversikt.Ytelse.FORELDREPENGER;
        }
    }

    private static void hentInnholdOpplastedeVedlegg(List<Vedlegg> vedlegg, List<Part> vedleggParts) {
        var vedleggsinnhold = hentInnholdFraVedleggPart(vedleggParts);
        vedlegg.stream()
            .filter(v -> v.getMetadata().innsendingsType() == null || LASTET_OPP.equals(v.getMetadata().innsendingsType()))
            .forEach(v -> v.setInnhold(vedleggsinnhold.get(v.getId())));
    }

    private static Map<String, byte[]> hentInnholdFraVedleggPart(List<Part> vedlegg) {
        var vedleggsreferanseTilInnholdMap = new HashMap<String, byte[]>();
        if (vedlegg == null) {
            return vedleggsreferanseTilInnholdMap;
        }

        for (var parten : vedlegg) {
            try (var is = parten.getInputStream())  {
                var vedleggsreferanse = parten.getHeader(VEDLEGG_REFERANSE_HEADER);
                vedleggsreferanseTilInnholdMap.put(vedleggsreferanse, is.readAllBytes());
            } catch (IOException e) {
                throw new IllegalStateException("Noe gikk galt med lesing av innhold i vedlegg", e);
            }

        }
        return vedleggsreferanseTilInnholdMap;
    }
    private static void validerRiktigAntallVedlegg(List<Vedlegg> vedleggSøknad, List<Part> vedleggParts) {
        var antallVedlegg = safeStream(vedleggSøknad)
            .filter(v -> LASTET_OPP.equals(v.getInnsendingsType()))
            .count();
        var antallVedleggPart = vedleggParts != null ? vedleggParts.size() : 0;
        if (antallVedlegg != antallVedleggPart) {
            LOG.info("Vedlegg i søknaden ({}) er {} mot {} vedlegg i part", antallVedlegg, vedleggSøknad, antallVedleggPart);
            throw new IllegalStateException("Utviklerfeil: Antall opplastede vedlegg i søknad " + antallVedlegg + " matcher IKKE antall vedlegg sendt i vedlegg part " + antallVedleggPart);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdl=" + oversikt + ", søknadSender="
                + søknadSender +"]";
    }


}
