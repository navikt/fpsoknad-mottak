package no.nav.foreldrepenger.mottak.fpfordel;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.VedleggSkjemanummer.SØKNAD_FOELDREPEMGER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelMetdataGenerator.Files;

@JsonPropertyOrder({ "forsendelsesId", "brukerId", "forsendelseMottatt", "filer" })
public class FPFordelMetadata {
    private final String forsendelsesId;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private final LocalDateTime forsendelseMottatt;
    private final String brukerId;
    @JsonInclude(value = Include.NON_EMPTY)
    private final List<Files> filer;

    public FPFordelMetadata(Søknad søknad, AktorId aktorId, String ref) {
        this.forsendelsesId = ref;
        this.brukerId = aktorId.getId();
        this.forsendelseMottatt = LocalDateTime.now();
        this.filer = files(søknad);
    }

    public List<Files> getFiler() {
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

    private static List<Files> files(Søknad søknad) {
        final AtomicInteger id = new AtomicInteger(1);
        List<Files> dokumenter = newArrayList(søknad(id), søknad(id));
        dokumenter.addAll(søknad.getVedlegg().stream()
                .map(s -> vedlegg(s, id))
                .collect(toList()));
        return dokumenter;
    }

    private static Files søknad(final AtomicInteger id) {
        return new Files(SØKNAD_FOELDREPEMGER, id.getAndIncrement());
    }

    private static Files vedlegg(Vedlegg vedlegg, final AtomicInteger id) {
        return new Files(vedlegg.getMetadata().getSkjemanummer(), id.getAndIncrement());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [forsendelsesId=" + forsendelsesId + ", forsendelseMottatt="
                + forsendelseMottatt + ", brukerId=" + brukerId + ", filer=" + filer + "]";
    }
}