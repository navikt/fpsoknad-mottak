package no.nav.foreldrepenger.mottak.dokmot;

import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.XMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.domain.XMLSøknadGenerator;
import no.nav.foreldrepenger.mottak.pdf.PdfGenerator;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Arkivfiltyper;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Behandlingstema;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentinnhold;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Forsendelsesinformasjon;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Hoveddokument;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Mottakskanaler;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Person;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Tema;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Variantformater;

@Service
public class DokmotXMLKonvoluttGenerator implements XMLKonvoluttGenerator {

    @Override
    public XMLSøknadGenerator getSøknadGenerator() {
        return søknadGenerator;
    }

    private final Marshaller marshaller;
    private final XMLSøknadGenerator søknadGenerator;
    private final PdfGenerator pdfGenerator;

    @Inject
    public DokmotXMLKonvoluttGenerator(XMLSøknadGenerator generator, PdfGenerator pdfFenerator) {
        this(marshaller(), generator, pdfFenerator);
    }

    private DokmotXMLKonvoluttGenerator(Marshaller marshaller, XMLSøknadGenerator generator,
            PdfGenerator pdfGenerator) {
        this.marshaller = marshaller;
        this.søknadGenerator = generator;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public String toXML(Søknad søknad) {
        return toXML(toDokmotModel(søknad));
    }

    @Override
    public Dokumentforsendelse toDokmotModel(Søknad søknad) {
        return dokumentForsendelse(søknad);
    }

    private Dokumentforsendelse dokumentForsendelse(Søknad søknad) {
        return new Dokumentforsendelse()
                .withHoveddokument(hoveddokument(søknad))
                .withForsendelsesinformasjon(new Forsendelsesinformasjon()
                        .withKanalreferanseId("TODO")
                        .withTema(new Tema().withValue("FOR"))
                        .withMottakskanal(new Mottakskanaler().withValue("NAV_NO"))
                        .withBehandlingstema(new Behandlingstema().withValue("ab0050"))
                        .withForsendelseInnsendt(LocalDateTime.now())
                        .withForsendelseMottatt(søknad.getMotattdato())
                        .withAvsender(new Person(søknad.getSøker().getFnr().getId()))
                        .withBruker(new Person(søknad.getSøker().getFnr().getId())))
                .withVedleggListe(dokmotVedleggListe(søknad.getPåkrevdeVedlegg(), søknad.getFrivilligeVedlegg()));
    }

    private String toXML(Dokumentforsendelse model) {
        try {
            StringWriter sw = new StringWriter();
            marshaller.marshal(model, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Hoveddokument hoveddokument(Søknad søknad) {
        Dokumentinnhold hovedskjemaInnhold = new Dokumentinnhold()
                .withDokument(pdfGenerator.generate(søknad))
                .withVariantformat(new Variantformater().withValue(Variant.ARKIV.name()));
        Stream<Dokumentinnhold> alternativeRepresentasjonerInnhold = Collections.singletonList(new Dokumentinnhold()
                .withDokument(søknadGenerator.toXML(søknad).getBytes())
                .withVariantformat(new Variantformater().withValue(Variant.ORIGINAL.name()))
                .withArkivfiltype(new Arkivfiltyper().withValue(Filtype.XML.name()))).stream();

        String skjemanummer = "NAV 14-05.07"; // Engangsstønad fødsel

        return new Hoveddokument()
                .withDokumenttypeId(SkjemanummerTilDokumentTypeKode.dokumentTypeKode(skjemanummer))
                .withDokumentinnholdListe(
                        Stream.concat(Stream.of(hovedskjemaInnhold), alternativeRepresentasjonerInnhold)
                                .collect(Collectors.toList()));
    }

    private List<no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg> dokmotVedleggListe(
            List<? extends Vedlegg> påkrevdeVedlegg, List<? extends Vedlegg> frivilligeVedlegg) {
        return Stream.concat(påkrevdeVedlegg.stream(), frivilligeVedlegg.stream())
                .map(v -> dokmotVedleggFraHenvendelseData(v))
                .collect(Collectors.toList());
    }

    private no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg dokmotVedleggFraHenvendelseData(Vedlegg vedlegg) {

        return new no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg()
                .withBrukeroppgittTittel(vedlegg.getMetadata().getBeskrivelse())
                .withDokumenttypeId(vedlegg.getMetadata().getSkjemanummer().dokumentTypeId())
                .withDokumentinnholdListe(new Dokumentinnhold()
                        .withVariantformat(new Variantformater().withValue(Variant.ARKIV.name()))
                        .withArkivfiltype(new Arkivfiltyper().withValue(vedlegg.getMetadata().getType().name()))
                        .withDokument(vedlegg.getVedlegg()));
    }

    private static JAXBContext context() {
        try {
            return JAXBContext.newInstance(Dokumentforsendelse.class);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Marshaller marshaller() {
        try {
            Marshaller marshaller = context().createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            return marshaller;
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public enum Filtype {
        PDF, PDFA, XML
    }

    public enum Variant {
        ARKIV, ORIGINAL
    }

}
