package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.INITIELL_FORELDREPENGER;

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
public class FPFordelSøknadSender implements SøknadSender {

    private final FPFordelConnection connection;
    private final FPFordelKonvoluttGenerator generator;
    private final InfoskrivPdfExtractor pdfExtractor;
    private final InnsendingDomainEventPublisher publisher;

    public FPFordelSøknadSender(FPFordelConnection connection, FPFordelKonvoluttGenerator generator,
            InfoskrivPdfExtractor pdfExtractor, InnsendingDomainEventPublisher publisher) {
        this.connection = connection;
        this.generator = generator;
        this.pdfExtractor = pdfExtractor;
        this.publisher = publisher;
    }

    @Override
    public Kvittering søk(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        Kvittering kvittering = doSend(søknad.getSøknadsRolle(), konvolutt(søknad, søker, egenskap));
        kvittering.setFørsteDag(søknad.getFørsteUttaksdag());
        kvittering.setFørsteInntektsmeldingDag(søknad.getFørsteInntektsmeldingDag());
        return kvittering;
    }

    @Override
    public Kvittering endreSøknad(Endringssøknad endring, Person søker, SøknadEgenskap egenskap) {
        Kvittering kvittering = doSend(endring.getSøknadsRolle(), konvolutt(endring, søker, egenskap));
        kvittering.setFørsteDag(endring.getFørsteUttaksdag());
        return kvittering;
    }

    @Override
    public Kvittering ettersend(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        return doSend(null, konvolutt(ettersending, søker, egenskap));
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    private Kvittering doSend(BrukerRolle rolle, FPFordelKonvolutt konvolutt) {
        Kvittering kvittering = connection.send(konvolutt.getEgenskap().getType(), rolle, konvolutt);
        if (INITIELL_FORELDREPENGER.equals(konvolutt.getEgenskap())) {
            kvittering.setInfoskrivPdf(pdfExtractor.extractInfoskriv(kvittering.getPdf()));
        }

        publisher.publishEvent(kvittering, konvolutt.getEgenskap(), konvolutt.getVedleggIds());
        return kvittering;

    }

    private FPFordelKonvolutt konvolutt(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return generator.generer(søknad, søker, egenskap);
    }

    private FPFordelKonvolutt konvolutt(Endringssøknad endring, Person søker, SøknadEgenskap egenskap) {
        return generator.generer(endring, søker, egenskap);
    }

    private FPFordelKonvolutt konvolutt(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        return generator.generer(ettersending, søker, egenskap);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", generator=" + generator
                + ", pdfExtractor=" + pdfExtractor + ", publisher=" + publisher + "]";
    }
}
