package no.nav.foreldrepenger.mottak.innsending;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;

import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.felles.EttersendingsType;
import no.nav.foreldrepenger.common.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.common.domain.felles.Person;
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

    @PostMapping("/send")
    public Kvittering initiell(@Valid @RequestBody Søknad søknad) {
        var søknadEgenskap = Inspektør.inspiser(søknad);
        validerFørstegangssøknad(søknad);
        var innsendingPersonInfo = personInfo(tilYtelse(søknad.getYtelse()));
        return søknadSender.søk(søknad, søknadEgenskap, innsendingPersonInfo);
    }

    @PostMapping(value = "/send/v2", consumes = { MediaType.MULTIPART_MIXED_VALUE })
    public Kvittering sendSøknad(@Valid @RequestPart("body") Søknad søknad,
                                 @RequestPart(value = "vedlegg", required = false) List<Part> vedlegg) throws IOException {
        var søknadEgenskap = Inspektør.inspiser(søknad);
        var vedleggsinnhold = hentInnholdFraVedleggPart(vedlegg);
        validerRiktigAntallVedlegg(søknad, vedleggsinnhold);
        validerFørstegangssøknad(søknad);
        var innsendingPersonInfo = personInfo(tilYtelse(søknad.getYtelse()));
        return søknadSender.søk(søknad, vedleggsinnhold, søknadEgenskap, innsendingPersonInfo);
    }

    private static void validerRiktigAntallVedlegg(Søknad søknad, Map<String, byte[]> vedleggsinnhold) {
        var antallVedlegg = safeStream(søknad.getVedlegg())
            .filter(v -> InnsendingsType.LASTET_OPP.equals(v.getInnsendingsType()))
            .count();
        if (antallVedlegg != vedleggsinnhold.size()) {
            throw new IllegalStateException("Utviklerfeil: Antall vedlegg i body " + antallVedlegg + "matcher IKKE antall vedlegg i vedlegg part " + vedleggsinnhold.size());
        }
    }

    private static Map<String, byte[]> hentInnholdFraVedleggPart(List<Part> vedlegg) throws IOException {
        var mappedevedlegg = new HashMap<String, byte[]>();
        if (vedlegg == null) {
            return mappedevedlegg;
        }

        for (var parten : vedlegg) {
            var vedleggsreferanse = parten.getHeader(VEDLEGG_REFERANSE_HEADER);
            var innhold = parten.getInputStream().readAllBytes();
            mappedevedlegg.put(vedleggsreferanse, innhold);
        }
        return mappedevedlegg;
    }

    private InnsendingPersonInfo map(Person person) {
        return new InnsendingPersonInfo(person.navn(), person.aktørId(), person.fnr());
    }

    @PostMapping("/ettersend")
    public Kvittering ettersend(@Valid @RequestBody Ettersending ettersending) {
        var innsendingPersonInfo = personInfo(tilYtelse(ettersending.type()));
        return søknadSender.ettersend(ettersending, Inspektør.inspiser(ettersending), innsendingPersonInfo);
    }

    @PostMapping("/endre")
    public Kvittering endre(@Valid @RequestBody Endringssøknad endringssøknad) {
        validerSøknad(endringssøknad.getYtelse());
        var innsendingPersonInfo = personInfo(tilYtelse(endringssøknad.getYtelse()));
        return søknadSender.endreSøknad(endringssøknad, ENDRING_FORELDREPENGER, innsendingPersonInfo);
    }

    private InnsendingPersonInfo personInfo(Ytelse ytelse) {
        var fnr = tokenUtil.autentisertBrukerOrElseThrowException();
        var person = pdl.hentPerson(fnr, ytelse);
        return map(person);
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdl=" + pdl + ", søknadSender="
                + søknadSender +"]";
    }


}
