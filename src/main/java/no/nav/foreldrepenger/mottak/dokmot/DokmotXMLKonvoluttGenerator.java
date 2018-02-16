package no.nav.foreldrepenger.mottak.dokmot;

import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.StringWriter;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.dokmot.DokmotData.Filtype;
import no.nav.foreldrepenger.mottak.dokmot.DokmotData.Variant;
import no.nav.foreldrepenger.mottak.domain.Søknad;
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
        return generator;
    }

    private final Marshaller marshaller;
    private final XMLSøknadGenerator generator;
    private final PdfGenerator pdfGenerator;

    @Inject
    public DokmotXMLKonvoluttGenerator(XMLSøknadGenerator generator, PdfGenerator pdfFenerator) {
        this(marshaller(), generator, pdfFenerator);
    }

    private DokmotXMLKonvoluttGenerator(Marshaller marshaller, XMLSøknadGenerator generator,
            PdfGenerator pdfGenerator) {
        this.marshaller = marshaller;
        this.generator = generator;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public String toXML(Søknad søknad) {
        return toXML(dokumentForsendelse(søknad, new DokmotData()));
    }

    private Dokumentforsendelse dokumentForsendelse(Søknad søknad, DokmotData data) {
        byte[] kvittering = pdfGenerator.generate(søknad);
        return new Dokumentforsendelse()
                .withHoveddokument(hoveddokument(søknad))
                .withForsendelsesinformasjon(new Forsendelsesinformasjon()
                        .withKanalreferanseId("TODO")
                        .withTema(new Tema().withValue("FOR"))
                        .withMottakskanal(new Mottakskanaler().withValue("NAV_NO"))
                        .withBehandlingstema(new Behandlingstema().withValue("ab0050"))
                        .withForsendelseInnsendt(søknad.getMotattdato())
                        .withForsendelseMottatt(søknad.getMotattdato())
                        .withAvsender(new Person(søknad.getSøker().getFnr().getId()))
                        .withBruker(new Person(søknad.getSøker().getFnr().getId())));

        // .withVedleggListe(dokmotVedleggListe(data.vedleggListe, data.aktorId)); */
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
                // .withDokument("MY PDF")
                .withVariantformat(new Variantformater().withValue(Variant.ARKIV.name()));
        Stream<Dokumentinnhold> alternativeRepresentasjonerInnhold = Collections.singletonList(new Dokumentinnhold()
                .withDokument(generator.toXML(søknad).getBytes())
                .withVariantformat(new Variantformater().withValue(Variant.ORIGINAL.name()))
                .withArkivfiltype(new Arkivfiltyper().withValue(Filtype.XML.name()))).stream();

        String skjemanummer = "NAV 14-05.07";

        return new Hoveddokument()
                .withDokumenttypeId(SkjemanummerTilDokumentTypeKode.dokumentTypeKode(skjemanummer))
                .withDokumentinnholdListe(
                        Stream.concat(Stream.of(hovedskjemaInnhold), alternativeRepresentasjonerInnhold)
                                .collect(Collectors.toList()));
    }

    /* private List<Vedlegg> dokmotVedleggListe(List<VedleggData> vedleggDataListe, String aktorId) { return
     * vedleggDataListe.stream() .map(v -> dokmotVedleggFraHenvendelseData(v, aktorId)) .collect(toList()); }
     *
     * private Vedlegg dokmotVedleggFraHenvendelseData(VedleggData vedlegg, String aktorId) { byte[] bytes = null; //
     * TODO fillager.hentFilForAktoer(vedlegg.uuid, aktorId); return new Vedlegg()
     * .withBrukeroppgittTittel(vedlegg.brukerTittel)
     * .withDokumenttypeId(SkjemanummerTilDokumentTypeKode.dokumentTypeKode(vedlegg.skjemanummer))
     * .withDokumentinnholdListe(new Dokumentinnhold() .withVariantformat(new
     * Variantformater().withValue(vedlegg.variant.name())) .withArkivfiltype(new
     * Arkivfiltyper().withValue(vedlegg.filtype.name())) .withDokument(bytes)); } */

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
}
