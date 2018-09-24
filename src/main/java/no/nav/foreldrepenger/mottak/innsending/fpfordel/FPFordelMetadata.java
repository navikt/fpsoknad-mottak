package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000002;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000005;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.EndringsSøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.RelasjonTilBarnMedVedlegg;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelMetdataGenerator.Filer;

@JsonPropertyOrder({ "forsendelsesId", "brukerId", "forsendelseMottatt", "filer" })
public class FPFordelMetadata {
    private final String forsendelsesId;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private final LocalDateTime forsendelseMottatt;
    private final String brukerId;
    private final List<Filer> filer;
    private final String saksnummer;

    public FPFordelMetadata(Ettersending ettersending, AktorId aktorId, String ref) {
        this(files(ettersending), aktorId, ref, ettersending.getSaksnr());
    }

    public FPFordelMetadata(Søknad søknad, AktorId aktorId, String ref) {
        this(søknad, aktorId, ref, null);
    }

    public FPFordelMetadata(EndringsSøknad endring, AktorId aktorId, String ref) {
        this(files(endring), aktorId, ref, endring.getSaksnr());
    }

    public FPFordelMetadata(Søknad søknad, AktorId aktorId, String ref, String saksnr) {
        this(files(søknad), aktorId, ref, saksnr);
    }

    public FPFordelMetadata(List<Filer> filer, AktorId aktorId, String ref, String saksnr) {
        this.forsendelsesId = ref;
        this.brukerId = aktorId.getId();
        this.forsendelseMottatt = LocalDateTime.now();
        this.filer = filer;
        this.saksnummer = saksnr;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public List<Filer> getFiler() {
        return filer;
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

    private static List<Filer> files(Søknad søknad) {
        final AtomicInteger id = new AtomicInteger(1);
        List<Filer> dokumenter = newArrayList(søknad(id, søknad), søknad(id, søknad));
        dokumenter.addAll(søknad.getVedlegg().stream()
                .map(s -> vedlegg(s, id))
                .collect(toList()));
        return dokumenter;
    }

    private static List<Filer> files(EndringsSøknad endring) {
        final AtomicInteger id = new AtomicInteger(1);
        List<Filer> dokumenter = newArrayList(søknad(id, endring), søknad(id, endring));
        dokumenter.addAll(endring.getVedlegg().stream()
                .map(s -> vedlegg(s, id))
                .collect(toList()));
        return dokumenter;
    }

    private static List<Filer> files(Ettersending ettersending) {
        AtomicInteger id = new AtomicInteger(1);
        return ettersending.getVedlegg().stream().map(s -> vedlegg(s, id)).collect(toList());

    }

    private static Filer søknad(final AtomicInteger id, Søknad søknad) {
        return new Filer(dokumentTypeFraRelasjon(søknad), id.getAndIncrement());
    }

    private static Filer søknad(final AtomicInteger id, EndringsSøknad søknad) {
        return new Filer(DokumentType.I000050, id.getAndIncrement());
    }

    private static Filer vedlegg(Vedlegg vedlegg, final AtomicInteger id) {
        return new Filer(vedlegg.getDokumentType(), id.getAndIncrement());
    }

    private static DokumentType dokumentTypeFraRelasjon(Søknad søknad) {
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
                + forsendelseMottatt + ", brukerId=" + brukerId + ", filer=" + filer + ", saksnummer=" + saksnummer
                + "]";
    }

}