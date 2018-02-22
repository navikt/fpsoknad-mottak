package no.nav.foreldrepenger.mottak.dokmot;

import static no.nav.foreldrepenger.mottak.dokmot.Variant.ARKIV;
import static no.nav.foreldrepenger.mottak.dokmot.Variant.ORIGINAL;
import static no.nav.foreldrepenger.mottak.domain.Filtype.PDFA;
import static no.nav.foreldrepenger.mottak.domain.Filtype.XML;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;

import org.springframework.stereotype.Service;

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

    private static final JAXBContext CONTEXT = Jaxb.context(Dokumentforsendelse.class);
    private final DokmotEngangsstønadXMLGenerator søknadGenerator;
    private static final Random r = new SecureRandom();

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
                        .withKanalreferanseId(String.valueOf(r.nextLong())) // TODO
                        .withTema(new Tema().withValue("FOR"))
                        .withMottakskanal(new Mottakskanaler().withValue("NAV_NO"))
                        .withBehandlingstema(new Behandlingstema().withValue("ab0050"))
                        .withForsendelseInnsendt(LocalDateTime.now())
                        .withForsendelseMottatt(søknad.getMotattdato())
                        .withAvsender(new Person(søknad.getSøker().getFnr().getId()))
                        .withBruker(new Person(søknad.getSøker().getFnr().getId())))
                .withHoveddokument(hoveddokument(søknad))
                .withVedleggListe(dokmotVedleggListe(søknad.getPåkrevdeVedlegg(), søknad.getFrivilligeVedlegg()));
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
                .map(this::dokmotVedlegg)
                .collect(Collectors.toList());
    }

    private no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg dokmotVedlegg(Vedlegg vedlegg) {

        return new no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg()
                .withBrukeroppgittTittel(vedlegg.getMetadata().getBeskrivelse())
                .withDokumenttypeId(vedlegg.getMetadata().getSkjemanummer().dokumentTypeId())
                .withDokumentinnholdListe(new Dokumentinnhold()
                        .withVariantformat(new Variantformater().withValue(ARKIV.name()))
                        .withArkivfiltype(new Arkivfiltyper().withValue(vedlegg.getMetadata().getType().name()))
                        .withDokument(vedlegg.getVedlegg()));
    }

    enum Variant {
        ARKIV, ORIGINAL
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [søknadGenerator=" + søknadGenerator + "]";
    }

}
