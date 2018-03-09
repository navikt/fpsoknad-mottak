package no.nav.foreldrepenger.mottak.dokmot;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.dokmot.ArkivVariant.ARKIV;
import static no.nav.foreldrepenger.mottak.dokmot.ArkivVariant.ORIGINAL;
import static no.nav.foreldrepenger.mottak.domain.Filtype.PDFA;
import static no.nav.foreldrepenger.mottak.domain.Filtype.XML;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.DokumentType;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.Vedlegg;
import no.nav.foreldrepenger.mottak.util.Jaxb;
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
public class DokmotEngangsstønadXMLKonvoluttGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(DokmotEngangsstønadXMLKonvoluttGenerator.class);

    private static final String FORELDREPENGER = "FOR";

    private static final String BEHANDLINGSTEMA = "ab0050";

    private static final String KANAL = "NAV_NO";

    private static final JAXBContext CONTEXT = Jaxb.context(Dokumentforsendelse.class);
    private final DokmotEngangsstønadXMLGenerator søknadGenerator;

    @Inject
    public DokmotEngangsstønadXMLKonvoluttGenerator(DokmotEngangsstønadXMLGenerator generator) {
        this.søknadGenerator = Objects.requireNonNull(generator);
    }

    public String toXML(Søknad søknad) {
        return xmlFra(dokmotModelFra(søknad));
    }

    public Dokumentforsendelse dokmotModelFra(Søknad søknad) {
        return dokumentForsendelseFra(søknad);
    }

    public String xmlFra(Dokumentforsendelse model) {
        return Jaxb.marshall(CONTEXT, model);
    }

    private Dokumentforsendelse dokumentForsendelseFra(Søknad søknad) {
        return new Dokumentforsendelse()
                .withForsendelsesinformasjon(new Forsendelsesinformasjon()
                        .withKanalreferanseId(referanseId())
                        .withTema(new Tema().withValue(FORELDREPENGER))
                        .withMottakskanal(new Mottakskanaler().withValue(KANAL))
                        .withBehandlingstema(new Behandlingstema().withValue(BEHANDLINGSTEMA))
                        .withForsendelseInnsendt(LocalDateTime.now())
                        .withForsendelseMottatt(søknad.getMottattdato())
                        .withAvsender(new Person(søknad.getSøker().getFnr().getId()))
                        .withBruker(new Person(søknad.getSøker().getFnr().getId())))
                .withHoveddokument(hoveddokument(søknad))
                .withVedleggListe(dokmotVedleggListe(søknad));
    }

    private Hoveddokument hoveddokument(Søknad søknad) {
        Dokumentinnhold hovedskjemaInnhold = new Dokumentinnhold()
                .withDokument(søknadGenerator.toPdf(søknad))
                .withArkivfiltype(new Arkivfiltyper().withValue(PDFA.name()))
                .withVariantformat(new Variantformater().withValue(ARKIV.name()));
        Stream<Dokumentinnhold> alternativeRepresentasjonerInnhold = Collections.singletonList(new Dokumentinnhold()
                .withDokument(søknadGenerator.toXML(søknad).getBytes())
                .withVariantformat(new Variantformater().withValue(ORIGINAL.name()))
                .withArkivfiltype(new Arkivfiltyper().withValue(XML.name()))).stream();

        return new Hoveddokument()
                .withDokumenttypeId(DokumentType.ENGANGSSTØNAD_FØDSEL.id)
                .withDokumentinnholdListe(
                        Stream.concat(Stream.of(hovedskjemaInnhold), alternativeRepresentasjonerInnhold)
                                .collect(toList()));
    }

    private static List<no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg> dokmotVedleggListe(Søknad søknad) {
        return Stream
                .concat(JukseVedlegg.påkrevdVedlegg(søknad) /* påkrevdeVedlegg */.stream(),
                        søknad.getFrivilligeVedlegg().stream())
                .map(DokmotEngangsstønadXMLKonvoluttGenerator::dokmotVedlegg).collect(toList());
    }

    private static no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg dokmotVedlegg(Vedlegg vedlegg) {

        return new no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg()
                .withBrukeroppgittTittel(vedlegg.getMetadata().getBeskrivelse())
                .withDokumenttypeId(vedlegg.getMetadata().getSkjemanummer().dokumentTypeId())
                .withDokumentinnholdListe(new Dokumentinnhold()
                        .withVariantformat(new Variantformater().withValue(ARKIV.name()))
                        .withArkivfiltype(new Arkivfiltyper().withValue(vedlegg.getMetadata().getType().name()))
                        .withDokument(vedlegg.getVedlegg()));
    }

    private static String referanseId() {
        return UUID.randomUUID().toString();
    }

    enum Variant {
        ARKIV, ORIGINAL
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [søknadGenerator=" + søknadGenerator + "]";
    }

}
