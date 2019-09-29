package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.innsending.pdf.InfoskrivPdfExtractor;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Service
public class FordelSøknadSender implements SøknadSender {

    private final FordelConnection connection;
    private final KonvoluttGenerator generator;
    private final InfoskrivPdfExtractor pdfExtractor;
    private final InnsendingHendelseProdusent publisher;

    public FordelSøknadSender(FordelConnection connection, KonvoluttGenerator generator,
            InfoskrivPdfExtractor pdfExtractor, InnsendingHendelseProdusent publisher) {
        this.connection = connection;
        this.generator = generator;
        this.pdfExtractor = pdfExtractor;
        this.publisher = publisher;
    }

    @Override
    public Kvittering søk(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return doSend(søknad.getSøknadsRolle(), konvolutt(søknad, søker, egenskap));
    }

    @Override
    public Kvittering endreSøknad(Endringssøknad endring, Person søker, SøknadEgenskap egenskap) {
        return doSend(endring.getSøknadsRolle(), konvolutt(endring, søker, egenskap));
    }

    @Override
    public Kvittering ettersend(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        return doSend(konvolutt(ettersending, søker, egenskap), ettersending.getReferanseId());
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    private Kvittering doSend(Konvolutt konvolutt, String referanseId) {
        return doSend(null, referanseId, konvolutt);
    }

    private Kvittering doSend(BrukerRolle rolle, Konvolutt konvolutt) {
        return doSend(rolle, null, konvolutt);
    }

    private Kvittering doSend(BrukerRolle rolle, String referanseId, Konvolutt konvolutt) {
        var kvittering = connection.send(konvolutt, rolle);
        if (konvolutt.erInitiellForeldrepenger()) {
            Søknad søknad = Søknad.class.cast(konvolutt.getInnsending());
            kvittering.setFørsteDag(søknad.getFørsteUttaksdag());
            kvittering.setFørsteInntektsmeldingDag(søknad.getFørsteInntektsmeldingDag());
            kvittering.setInfoskrivPdf(infoskrivPdf(kvittering.getPdf()));
        }
        if (konvolutt.erEndring()) {
            var es = Endringssøknad.class.cast(konvolutt.getInnsending());
            kvittering.setFørsteDag(es.getFørsteUttaksdag());
        }

        publisher.publiser(kvittering, referanseId, konvolutt.getType(), konvolutt.getVedleggIds());
        return kvittering;

    }

    private byte[] infoskrivPdf(byte[] pdf) {
        return pdfExtractor.extractInfoskriv(pdf);
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
                + ", pdfExtractor=" + pdfExtractor + ", publisher=" + publisher + "]";
    }
}
