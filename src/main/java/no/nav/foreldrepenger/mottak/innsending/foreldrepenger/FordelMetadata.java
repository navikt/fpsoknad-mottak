package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000001;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000002;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000003;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000004;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000005;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000050;
import static no.nav.foreldrepenger.common.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.common.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.domain.felles.DokumentType;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Omsorgsovertakelse;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.error.UnexpectedInputException;
import no.nav.foreldrepenger.common.innsending.SøknadType;

@JsonPropertyOrder({ "forsendelsesId", "saksnummer", "brukerId", "forsendelseMottatt", "filer" })
public record FordelMetadata(
    String forsendelsesId,
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") LocalDateTime forsendelseMottatt,
    String brukerId,
    List<@Valid Del> filer,
    @Valid Saksnummer saksnummer) {


    public FordelMetadata(Ettersending ettersending, AktørId aktorId, String ref) {
        this(ettersendingsDeler(ettersending), aktorId, ref, ettersending.saksnr());
    }

    public FordelMetadata(Søknad søknad, SøknadType søknadType, AktørId aktorId, String ref) {
        this(søknad, søknadType, aktorId, ref, null);
    }

    public FordelMetadata(Endringssøknad endringssøknad, SøknadType søknadType, AktørId aktorId, String ref) {
        this(endringssøknadsDeler(endringssøknad, søknadType), aktorId, ref, endringssøknad.getSaksnr());
    }

    public FordelMetadata(Søknad søknad, SøknadType søknadType, AktørId aktorId, String ref, Saksnummer saksnr) {
        this(søknadsDeler(søknad, søknadType), aktorId, ref, saksnr);
    }

    public FordelMetadata(List<Del> deler, AktørId aktorId, String ref, Saksnummer saksnummer) {
        this(ref, LocalDateTime.now(), aktorId.value(), deler, saksnummer);
    }

    private static List<Del> søknadsDeler(Søknad søknad, SøknadType type) {
        var id = new AtomicInteger(1);
        var dokumenter = new ArrayList<Del>();
        dokumenter.add(søknadsDel(id, søknad, type));
        dokumenter.add(søknadsDel(id, søknad, type));
        safeStream(søknad.getVedlegg())
            .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
            .map(s -> vedleggsDel(s, id))
            .forEach(dokumenter::add);
        return dokumenter;
    }

    private static List<Del> endringssøknadsDeler(Endringssøknad endringssøknad, SøknadType type) {
        var id = new AtomicInteger(1);
        var dokumenter = new ArrayList<Del>();
        dokumenter.add(endringsøknadsDel(id, type));
        dokumenter.add(endringsøknadsDel(id, type));
        safeStream(endringssøknad.getVedlegg())
            .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
            .map(s -> vedleggsDel(s, id))
            .forEach(dokumenter::add);
        return dokumenter;
    }

    private static List<Del> ettersendingsDeler(Ettersending ettersending) {
        var id = new AtomicInteger(1);
        return safeStream(ettersending.vedlegg())
                .map(s -> vedleggsDel(s, id))
                .toList();
    }

    private static Del søknadsDel(final AtomicInteger id, Søknad søknad, SøknadType type) {
        return new Del(dokumentTypeFraRelasjon(søknad, type), id.getAndIncrement());
    }

    private static Del endringsøknadsDel(final AtomicInteger id, SøknadType type) {
        if (type.equals(ENDRING_FORELDREPENGER)) {
            return new Del(I000050, id.getAndIncrement());
        }
        throw new UnexpectedInputException("Søknad av type %s ikke støttet", type);
    }

    private static Del vedleggsDel(Vedlegg vedlegg, final AtomicInteger id) {
        return new Del(vedlegg.getDokumentType(), id.getAndIncrement());
    }

    private static DokumentType dokumentTypeFraRelasjon(Søknad søknad, SøknadType type) {
        return switch (type.fagsakType()) {
            case FORELDREPENGER -> dokumentTypeFraRelasjonForForeldrepenger(søknad);
            case ENGANGSSTØNAD -> dokumentTypeFraRelasjonForEngangsstønad(søknad);
            case SVANGERSKAPSPENGER -> I000001;
            default -> throw new UnexpectedInputException("Søknad av type %s ikke støttet", type);
        };
    }

    private static DokumentType dokumentTypeFraRelasjonForEngangsstønad(Søknad søknad) {
        var relasjon = Engangsstønad.class.cast(søknad.getYtelse()).relasjonTilBarn();
        if (relasjon instanceof Fødsel || relasjon instanceof FremtidigFødsel) {
            return I000003;
        }
        if (relasjon instanceof Omsorgsovertakelse || relasjon instanceof Adopsjon) {
            return I000004;
        }
        throw new UnexpectedInputException("Ukjent relasjon %s", relasjon.getClass().getSimpleName());
    }

    private static DokumentType dokumentTypeFraRelasjonForForeldrepenger(Søknad søknad) {
        var relasjon = Foreldrepenger.class.cast(søknad.getYtelse()).relasjonTilBarn();
        if (relasjon instanceof Fødsel || relasjon instanceof FremtidigFødsel) {
            return I000005;
        }

        if (relasjon instanceof Adopsjon || relasjon instanceof Omsorgsovertakelse) {
            return I000002;
        }
        throw new UnexpectedInputException("Ukjent relasjon %s", relasjon.getClass().getSimpleName());
    }

}
