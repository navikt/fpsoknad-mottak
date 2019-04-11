package no.nav.foreldrepenger.mottak.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.AVSLÅTT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.GOSYS;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.INNVILGET;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅGÅR;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅ_VENT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.ACCEPTED;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FAILED;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.PENDING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.REJECTED;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.RUNNING;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static no.nav.foreldrepenger.mottak.util.StringUtil.limit;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelGosysKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPSakFordeltKvittering;
import no.nav.foreldrepenger.mottak.innsyn.ForsendelsesStatusKvittering;

@Data
@JsonInclude(NON_NULL)
public class Kvittering {

    private static final Logger LOG = LoggerFactory.getLogger(Kvittering.class);
    private final String referanseId;
    private final LocalDateTime mottattDato;
    private LocalDate førsteDag;
    private final LeveranseStatus leveranseStatus;
    private String journalId;
    private String saksNr;
    private byte[] pdf;

    public Kvittering(LeveranseStatus leveranseStatus) {
        this(leveranseStatus, LocalDateTime.now(), callId());
    }

    @JsonCreator
    public Kvittering(@JsonProperty("leveranseStatus") LeveranseStatus leveranseStatus,
            @JsonProperty("mottattDato") LocalDateTime mottattDato,
            @JsonProperty("referanseId") String referanseId) {
        this.referanseId = referanseId;
        this.mottattDato = mottattDato;
        this.leveranseStatus = leveranseStatus;
    }

    public boolean erVellykket() {
        return leveranseStatus.erVellykket();
    }

    public static Kvittering ikkeSendt(byte[] pdf) {
        Kvittering kvittering = new Kvittering(IKKE_SENDT_FPSAK);
        kvittering.setPdf(pdf);
        return kvittering;
    }

    public static Kvittering kvitteringMedType(LeveranseStatus type, String journalId, String saksnr) {
        Kvittering kvittering = new Kvittering(type);
        kvittering.setJournalId(journalId);
        kvittering.setSaksNr(saksnr);
        return kvittering;
    }

    public static Kvittering forsendelsesStatusKvittering(ForsendelsesStatusKvittering forsendelsesStatus,
            FPSakFordeltKvittering fordeltKvittering) {

        switch (forsendelsesStatus.getForsendelseStatus()) {
            case AVSLÅTT:
                REJECTED.increment();
                return kvitteringMedType(AVSLÅTT, fordeltKvittering.getJournalpostId(),
                        fordeltKvittering.getSaksnummer());
            case INNVILGET:
                ACCEPTED.increment();
                return kvitteringMedType(INNVILGET, fordeltKvittering.getJournalpostId(),
                        fordeltKvittering.getSaksnummer());
            case MOTTATT:
            case PÅ_VENT:
                PENDING.increment();
                return kvitteringMedType(PÅ_VENT, fordeltKvittering.getJournalpostId(),
                        fordeltKvittering.getSaksnummer());
            case PÅGÅR:
                RUNNING.increment();
                return kvitteringMedType(PÅGÅR, fordeltKvittering.getJournalpostId(),
                        fordeltKvittering.getSaksnummer());
            default:
                LOG.warn("Fikk forsendelsesstatus {}", forsendelsesStatus.getForsendelseStatus());
                FAILED.increment();
                return new Kvittering(FP_FORDEL_MESSED_UP);
        }
    }

    public static Kvittering sendtOgForsøktBehandletKvittering(FPSakFordeltKvittering kvittering) {
        LOG.info("Søknaden er motatt og forsøkt behandlet av FPSak, journalId er {}, saksnummer er {}",
                kvittering.getJournalpostId(), kvittering.getSaksnummer());
        FAILED.increment();
        return kvitteringMedType(SENDT_OG_FORSØKT_BEHANDLET_FPSAK, kvittering.getJournalpostId(),
                kvittering.getSaksnummer());
    }

    public static Kvittering gosysKvittering(FPFordelGosysKvittering gosysKvittering) {
        LOG.info("Søknaden er sendt til manuell behandling i Gosys, journalId er {}",
                gosysKvittering.getJournalpostId());
        return kvitteringMedType(GOSYS, gosysKvittering.getJournalpostId(), null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [referanseId=" + referanseId + ", mottattDato=" + mottattDato
                + ", førsteDag=" + førsteDag + ", leveranseStatus=" + leveranseStatus + ", journalId=" + journalId
                + ", saksNr=" + saksNr + ", pdf=" + limit(pdf, 150) + "]";
    }
}
