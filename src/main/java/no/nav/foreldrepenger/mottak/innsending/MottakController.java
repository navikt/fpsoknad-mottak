package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.common.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.common.innsending.SøknadEgenskap.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.innsending.SøknadValidator.validerFørstegangssøknad;
import static no.nav.foreldrepenger.mottak.innsending.SøknadValidator.validerSøknad;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.Ytelse.ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.Ytelse.FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.Ytelse.SVANGERSKAPSPENGER;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;
import no.nav.foreldrepenger.mottak.oppslag.pdl.Ytelse;

@ProtectedRestController(MottakController.INNSENDING)
public class MottakController {
    public static final String INNSENDING = "/mottak";
    private static final String VEDLEGG_REFERANSE_HEADER = "vedleggsreferanse";
    private static final String BODY_PART_NAME = "body";
    private static final String VEDLEGG_PART_NAME = "vedlegg";

    private final PDLConnection pdl;
    private final SøknadSender søknadSender;
    private final TokenUtil tokenUtil;

    public MottakController(SøknadSender søknadSender,
                            PDLConnection pdl,
                            TokenUtil tokenUtil) {
        this.søknadSender = søknadSender;
        this.pdl = pdl;
        this.tokenUtil = tokenUtil;
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



    private InnsendingPersonInfo personInfo(Ytelse ytelse) {
        var fnr = tokenUtil.autentisertBrukerOrElseThrowException();
        var person = pdl.hentPerson(fnr, ytelse);
        return map(person);
    }

    private static InnsendingPersonInfo map(Person person) {
        return new InnsendingPersonInfo(person.navn(), person.aktørId(), person.fnr());
    }

    private static Ytelse tilYtelse(EttersendingsType ettersendingsType) {
        return switch (ettersendingsType) {
            case ENGANGSSTØNAD -> ENGANGSSTØNAD;
            case FORELDREPENGER -> FORELDREPENGER;
            case SVANGERSKAPSPENGER -> SVANGERSKAPSPENGER;
        };
    }
    private static Ytelse tilYtelse(no.nav.foreldrepenger.common.domain.Ytelse ytelse) {
        if (ytelse instanceof Engangsstønad) {
            return ENGANGSSTØNAD;
        } else if (ytelse instanceof Svangerskapspenger) {
            return SVANGERSKAPSPENGER;
        } else {
            return FORELDREPENGER;
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
    private static void validerRiktigAntallVedlegg(List<Vedlegg> vedlegg, List<Part> vedleggParts) {
        var antallVedlegg = safeStream(vedlegg)
            .filter(v -> LASTET_OPP.equals(v.getInnsendingsType()))
            .count();
        var antallVedleggPart = vedleggParts != null ? vedleggParts.size() : 0;
        if (antallVedlegg != antallVedleggPart) {
            throw new IllegalStateException("Utviklerfeil: Antall opplastede vedlegg i søknad " + antallVedlegg + " matcher IKKE antall vedlegg sendt i vedlegg part " + antallVedleggPart);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdl=" + pdl + ", søknadSender="
                + søknadSender +"]";
    }


}
