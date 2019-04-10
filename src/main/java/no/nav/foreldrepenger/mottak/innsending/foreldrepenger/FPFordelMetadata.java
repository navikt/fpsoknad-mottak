package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000001;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000002;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000003;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000005;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000050;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;

@JsonPropertyOrder({ "forsendelsesId", "saksnummer", "brukerId", "forsendelseMottatt", "filer" })
public class FPFordelMetadata {
    private final String forsendelsesId;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private final LocalDateTime forsendelseMottatt;
    private final String brukerId;
    @JsonProperty("filer")
    private final List<Del> deler;
    private final String saksnummer;

    public FPFordelMetadata(Ettersending ettersending, AktørId aktorId, String ref) {
        this(ettersendingsDeler(ettersending), aktorId, ref, ettersending.getSaksnr());
    }

    public FPFordelMetadata(Søknad søknad, SøknadType søknadType, AktørId aktorId, String ref) {
        this(søknad, søknadType, aktorId, ref, null);
    }

    public FPFordelMetadata(Endringssøknad endringssøknad, SøknadType søknadType, AktørId aktorId, String ref) {
        this(endringssøknadsDeler(endringssøknad, søknadType), aktorId, ref, endringssøknad.getSaksnr());
    }

    public FPFordelMetadata(Søknad søknad, SøknadType søknadType, AktørId aktorId, String ref, String saksnr) {
        this(søknadsDeler(søknad, søknadType), aktorId, ref, saksnr);
    }

    public FPFordelMetadata(List<Del> deler, AktørId aktorId, String ref, String saksnr) {
        this.forsendelsesId = ref;
        this.brukerId = aktorId.getId();
        this.forsendelseMottatt = LocalDateTime.now();
        this.deler = deler;
        this.saksnummer = saksnr;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public List<Del> getFiler() {
        return deler;
    }

    public String getForsendelsesId() {
        return forsendelsesId;
    }

    public LocalDateTime getForsendelseMottatt() {
        return forsendelseMottatt;
    }

    public String getBrukerId() {
        return brukerId;
    }

    private static List<Del> søknadsDeler(Søknad søknad, SøknadType søknadType) {
        final AtomicInteger id = new AtomicInteger(1);
        List<Del> dokumenter = newArrayList(søknadsDel(id, søknad, søknadType), søknadsDel(id, søknad, søknadType));
        dokumenter.addAll(safeStream(søknad.getVedlegg())
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .map(s -> vedleggsDel(s, id))
                .collect(toList()));
        return dokumenter;
    }

    private static List<Del> endringssøknadsDeler(Endringssøknad endringssøknad, SøknadType søknadType) {
        final AtomicInteger id = new AtomicInteger(1);
        List<Del> dokumenter = newArrayList(endringsøknadsDel(id, søknadType),
                endringsøknadsDel(id, søknadType));
        dokumenter.addAll(safeStream(endringssøknad.getVedlegg())
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .map(s -> vedleggsDel(s, id))
                .collect(toList()));
        return dokumenter;
    }

    private static List<Del> ettersendingsDeler(Ettersending ettersending) {
        AtomicInteger id = new AtomicInteger(1);
        return safeStream(ettersending.getVedlegg())
                .map(s -> vedleggsDel(s, id))
                .collect(toList());
    }

    private static Del søknadsDel(final AtomicInteger id, Søknad søknad, SøknadType søknadType) {
        return new Del(dokumentTypeFraRelasjon(søknad, søknadType), id.getAndIncrement());
    }

    private static Del endringsøknadsDel(final AtomicInteger id, SøknadType søknadType) {
        if (søknadType.equals(ENDRING_FORELDREPENGER)) {
            return new Del(I000050, id.getAndIncrement());
        }
        throw new UnexpectedInputException("Søknad av type %s ikke støttet", søknadType);
    }

    private static Del vedleggsDel(Vedlegg vedlegg, final AtomicInteger id) {
        return new Del(vedlegg.getDokumentType(), id.getAndIncrement());
    }

    private static DokumentType dokumentTypeFraRelasjon(Søknad søknad, SøknadType søknadType) {
        switch (søknadType.fagsakType()) {
            case FORELDREPENGER:
                return dokumentTypeFraRelasjonForForeldrepenger(søknad);
            case ENGANGSSTØNAD:
                return dokumentTypeFraRelasjonForEngangsstønad(søknad);
            case SVANGERSKAPSPENGER:
                return I000001;
            default:
                throw new UnexpectedInputException("Søknad av type %s ikke støttet", søknadType);
        }
    }

    private static DokumentType dokumentTypeFraRelasjonForEngangsstønad(Søknad søknad) {
        RelasjonTilBarn relasjon = ((Engangsstønad) søknad.getYtelse()).getRelasjonTilBarn();
        if (relasjon instanceof Fødsel
                || relasjon instanceof FremtidigFødsel) {
            return I000003;
        }
        if (relasjon instanceof Omsorgsovertakelse
                || relasjon instanceof Adopsjon) {
            return I000003; // TODO separate type ?
        }
        throw new UnexpectedInputException("Ukjent relasjon %s", relasjon.getClass().getSimpleName());
    }

    private static DokumentType dokumentTypeFraRelasjonForForeldrepenger(Søknad søknad) {
        RelasjonTilBarn relasjon = ((Foreldrepenger) søknad.getYtelse()).getRelasjonTilBarn();
        if (relasjon instanceof Fødsel || relasjon instanceof FremtidigFødsel) {
            return I000005;
        }

        if (relasjon instanceof Adopsjon || relasjon instanceof Omsorgsovertakelse) {
            return I000002;
        }
        throw new UnexpectedInputException("Ukjent relasjon %s", relasjon.getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [forsendelsesId=" + forsendelsesId + ", forsendelseMottatt="
                + forsendelseMottatt + ", brukerId=" + brukerId + ", deler=" + deler + ", saksnummer=" + saksnummer
                + "]";
    }

}
