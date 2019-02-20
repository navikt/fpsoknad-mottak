package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000002;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000003;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000005;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000050;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ENDRING_FORELDREPENGER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.RelasjonTilBarnMedVedlegg;
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

    public FPFordelMetadata(Ettersending ettersending, AktorId aktorId, String ref) {
        this(ettersendingsDeler(ettersending), aktorId, ref, ettersending.getSaksnr());
    }

    public FPFordelMetadata(Søknad søknad, SøknadType søknadType, AktorId aktorId, String ref) {
        this(søknad, søknadType, aktorId, ref, null);
    }

    public FPFordelMetadata(Endringssøknad endringssøknad, SøknadType søknadType, AktorId aktorId, String ref) {
        this(endringssøknadsDeler(endringssøknad, søknadType), aktorId, ref, endringssøknad.getSaksnr());
    }

    public FPFordelMetadata(Søknad søknad, SøknadType søknadType, AktorId aktorId, String ref, String saksnr) {
        this(søknadsDeler(søknad, søknadType), aktorId, ref, saksnr);
    }

    public FPFordelMetadata(List<Del> deler, AktorId aktorId, String ref, String saksnr) {
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
        dokumenter.addAll(søknad.getVedlegg().stream()
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .map(s -> vedleggsDel(s, id))
                .collect(toList()));
        return dokumenter;
    }

    private static List<Del> endringssøknadsDeler(Endringssøknad endringssøknad, SøknadType søknadType) {
        final AtomicInteger id = new AtomicInteger(1);
        List<Del> dokumenter = newArrayList(endringsøknadsDel(id, endringssøknad, søknadType),
                endringsøknadsDel(id, endringssøknad, søknadType));
        dokumenter.addAll(endringssøknad.getVedlegg().stream()
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .map(s -> vedleggsDel(s, id))
                .collect(toList()));
        return dokumenter;
    }

    private static List<Del> ettersendingsDeler(Ettersending ettersending) {
        AtomicInteger id = new AtomicInteger(1);
        return ettersending.getVedlegg().stream()
                .map(s -> vedleggsDel(s, id))
                .collect(toList());
    }

    private static Del søknadsDel(final AtomicInteger id, Søknad søknad, SøknadType søknadType) {
        return new Del(dokumentTypeFraRelasjon(søknad, søknadType), id.getAndIncrement());
    }

    private static Del endringsøknadsDel(final AtomicInteger id, Endringssøknad søknad, SøknadType søknadType) {
        if (søknadType.equals(ENDRING_FORELDREPENGER)) {
            return new Del(I000050, id.getAndIncrement());
        }
        throw new UnsupportedOperationException("Søknad av type " + søknadType + " foreløpig ikke støttet");
    }

    private static Del vedleggsDel(Vedlegg vedlegg, final AtomicInteger id) {
        return new Del(vedlegg.getDokumentType(), id.getAndIncrement());
    }

    private static DokumentType dokumentTypeFraRelasjon(Søknad søknad, SøknadType søknadType) {
        if (søknadType.erForeldrepenger()) {
            return dokumentTypeFraRelasjonForForeldrepenger(søknad);
        }
        if (søknadType.erEngangsstønad()) {
            return dokumentTypeFraRelasjonForEngangsstønad(søknad);
        }
        throw new UnsupportedOperationException(
                "Ytelse av type " + søknad.getYtelse().getClass().getSimpleName() + " ikke støttet");
    }

    private static DokumentType dokumentTypeFraRelasjonForEngangsstønad(Søknad søknad) {
        RelasjonTilBarn relasjon = Engangsstønad.class.cast(søknad.getYtelse()).getRelasjonTilBarn();
        if (relasjon instanceof no.nav.foreldrepenger.mottak.domain.felles.Fødsel
                || relasjon instanceof no.nav.foreldrepenger.mottak.domain.felles.FremtidigFødsel) {
            return I000003;
        }
        if (relasjon instanceof no.nav.foreldrepenger.mottak.domain.felles.Omsorgsovertakelse
                || relasjon instanceof no.nav.foreldrepenger.mottak.domain.felles.Adopsjon) {
            return I000003; // DOTO separate type ?
        }
        throw new IllegalArgumentException("Ukjent relasjon " + relasjon.getClass().getSimpleName());
    }

    private static DokumentType dokumentTypeFraRelasjonForForeldrepenger(Søknad søknad) {
        RelasjonTilBarnMedVedlegg relasjon = Foreldrepenger.class.cast(søknad.getYtelse()).getRelasjonTilBarn();
        if (relasjon instanceof Fødsel || relasjon instanceof FremtidigFødsel) {
            return I000005;
        }

        if (relasjon instanceof Adopsjon || relasjon instanceof Omsorgsovertakelse) {
            return I000002;
        }
        throw new IllegalArgumentException("Ukjent relasjon " + relasjon.getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [forsendelsesId=" + forsendelsesId + ", forsendelseMottatt="
                + forsendelseMottatt + ", brukerId=" + brukerId + ", deler=" + deler + ", saksnummer=" + saksnummer
                + "]";
    }

}