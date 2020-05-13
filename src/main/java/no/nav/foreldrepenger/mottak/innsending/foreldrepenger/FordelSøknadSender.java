package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.innsending.pdf.InfoskrivPdfEkstraktor;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Service
public class FordelSøknadSender implements SøknadSender {

    private static final Logger LOG = LoggerFactory.getLogger(FordelSøknadSender.class);
    private final FordelConnection connection;
    private final KonvoluttGenerator generator;
    private final InfoskrivPdfEkstraktor ekstraktor;
    private final InnsendingHendelseProdusent hendelser;
    private final TokenUtil tokenUtil;

    public FordelSøknadSender(FordelConnection connection, KonvoluttGenerator generator,
            InfoskrivPdfEkstraktor ekstraktor, InnsendingHendelseProdusent hendelseProdusent, TokenUtil tokenUtil) {
        this.connection = connection;
        this.generator = generator;
        this.ekstraktor = ekstraktor;
        this.hendelser = hendelseProdusent;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public Kvittering søk(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return send(konvolutt(søknad, søker, egenskap), søknad.getSøknadsRolle());
    }

    @Override
    public Kvittering endreSøknad(Endringssøknad endring, Person søker, SøknadEgenskap egenskap) {
        return send(konvolutt(endring, søker, egenskap), endring.getSøknadsRolle());
    }

    @Override
    public Kvittering ettersend(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        return send(konvolutt(ettersending, søker, egenskap), ettersending.getDialogId());
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    private Kvittering send(Konvolutt konvolutt, String dialogId) {
        return send(konvolutt, null, dialogId);
    }

    private Kvittering send(Konvolutt konvolutt, BrukerRolle rolle) {
        return send(konvolutt, rolle, null);
    }

    private Kvittering send(Konvolutt konvolutt, BrukerRolle rolle, String dialogId) {
        var kvittering = connection.send(konvolutt, rolle);
        if (konvolutt.erInitiellForeldrepenger()) {
            Søknad søknad = Søknad.class.cast(konvolutt.getInnsending());
            kvittering.setFørsteDag(søknad.getFørsteUttaksdag());
            kvittering.setFørsteInntektsmeldingDag(søknad.getFørsteInntektsmeldingDag());
            kvittering.setInfoskrivPdf(infoskrivPdf(kvittering.getPdf()));
        }
        if (konvolutt.erEndring()) {
            kvittering.setFørsteDag(Endringssøknad.class.cast(konvolutt.getInnsending()).getFørsteUttaksdag());
        }
        publiserHendelse(konvolutt, dialogId, kvittering);
        return kvittering;
    }

    private void publiserHendelse(Konvolutt konvolutt, String dialogId, Kvittering kvittering) {
        try {
            hendelser.publiser(tokenUtil.autentisertFNR(), kvittering, dialogId, konvolutt);
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
