package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.innsending.pdf.InfoskrivPdfEkstraktor;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Service
public class FordelSøknadSender implements SøknadSender {

    private static final Logger LOG = LoggerFactory.getLogger(FordelSøknadSender.class);
    private final FordelConnection connection;
    private final KonvoluttGenerator generator;
    private final InfoskrivPdfEkstraktor ekstraktor;
    private final InnsendingHendelseProdusent hendelser;
    private final TokenUtil tokenUtil;

    public FordelSøknadSender(FordelConnection connection,
                              KonvoluttGenerator generator,
                              InfoskrivPdfEkstraktor ekstraktor,
                              InnsendingHendelseProdusent hendelseProdusent,
                              TokenUtil tokenUtil) {
        this.connection = connection;
        this.generator = generator;
        this.ekstraktor = ekstraktor;
        this.hendelser = hendelseProdusent;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public Kvittering søk(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return send(konvolutt(søknad, søker, egenskap));
    }

    @Override
    public Kvittering endreSøknad(Endringssøknad endring, Person søker, SøknadEgenskap egenskap) {
        return send(konvolutt(endring, søker, egenskap));
    }

    @Override
    public Kvittering ettersend(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        return send(konvolutt(ettersending, søker, egenskap), ettersending.getDialogId());
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    private Kvittering send(Konvolutt konvolutt) {
        return send(konvolutt, null);
    }

    private Kvittering send(Konvolutt konvolutt, String dialogId) {
        var pdfHovedDokument = konvolutt.PDFHovedDokument();
        var infoskrivPdf = konvolutt.erInitiellForeldrepenger() ? infoskrivPdf(pdfHovedDokument) : null;
        var mottattDato = LocalDateTime.now();
        FordelResultat fordelKvittering;
        try {
            fordelKvittering = connection.send(konvolutt);
        } catch (UventetFpFordelResponseException e) {
            LOG.warn("Uventet response fra fpfordel. Returnerer kvittering uten saksnummer", e);
            return new Kvittering(mottattDato, null, pdfHovedDokument, infoskrivPdf);
        }

        publiserHendelse(konvolutt, dialogId, fordelKvittering);

        return new Kvittering(mottattDato, fordelKvittering.saksnummer(), pdfHovedDokument, infoskrivPdf);
    }

    private void publiserHendelse(Konvolutt konvolutt, String dialogId, FordelResultat kvittering) {
        try {
            hendelser.publiser(tokenUtil.autentisertBruker(), kvittering, dialogId, konvolutt);
        } catch (Exception e) {
            LOG.warn("Kunne ikke publisere hendelse", e);
        }
    }

    private byte[] infoskrivPdf(byte[] pdf) {
        return ekstraktor.infoskriv(pdf);
    }

    private Konvolutt konvolutt(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return generator.generer(søknad, søker, egenskap);
    }

    private Konvolutt konvolutt(Endringssøknad endring, Person søker, SøknadEgenskap egenskap) {
        return generator.generer(endring, søker, egenskap);
    }

    private Konvolutt konvolutt(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        return generator.generer(ettersending, søker, egenskap);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", generator=" + generator
                + ", ekstraktor=" + ekstraktor + ", hendelseProdusent=" + hendelser + "]";
    }
}
