package no.nav.foreldrepenger.mottak.innsending.engangsstønad;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.Filtype.PDFA;
import static no.nav.foreldrepenger.mottak.domain.Filtype.XML;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000003;
import static no.nav.foreldrepenger.mottak.http.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.mottak.innsending.engangsstønad.ArkivVariant.ARKIV;
import static no.nav.foreldrepenger.mottak.innsending.engangsstønad.ArkivVariant.ORIGINAL;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Filtype;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.util.Jaxb;
import no.nav.foreldrepenger.mottak.util.Jaxb.ValidationMode;
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

    private static final String TEMA = "FOR";

    private static final String BEHANDLINGSTEMA = "ab0050";

    private static final String KANAL = "NAV_NO";

    private final DokmotEngangsstønadXMLGenerator søknadGenerator;

    public DokmotEngangsstønadXMLKonvoluttGenerator(DokmotEngangsstønadXMLGenerator generator) {
        this.søknadGenerator = Objects.requireNonNull(generator);
    }

    public String tilXML(Søknad søknad, no.nav.foreldrepenger.mottak.domain.felles.Person søker) {

        return toXML(søknad, søker, true);
    }

    public String toXML(Søknad søknad, no.nav.foreldrepenger.mottak.domain.felles.Person søker,
            boolean inkluderVedlegg) {
        return Jaxb.marshal(dokmotModelFra(søknad, søker, inkluderVedlegg),
                ValidationMode.ENGANGSSTØNAD);
    }

    public Dokumentforsendelse dokmotModelFra(Søknad søknad, no.nav.foreldrepenger.mottak.domain.felles.Person søker,
            boolean inkluderVedlegg) {
        return dokumentForsendelseFra(søknad, søker);
    }

    public String toSøknadsXML(Søknad søknad, no.nav.foreldrepenger.mottak.domain.felles.Person søker) {
        return søknadGenerator.tilXML(søknad, søker);
    }

    private Dokumentforsendelse dokumentForsendelseFra(Søknad søknad,
            no.nav.foreldrepenger.mottak.domain.felles.Person søker) {
        return new Dokumentforsendelse()
                .withForsendelsesinformasjon(new Forsendelsesinformasjon()
                        .withKanalreferanseId(MDC.get(NAV_CALL_ID))
                        .withTema(new Tema().withValue(TEMA))
                        .withMottakskanal(new Mottakskanaler().withValue(KANAL))
                        .withBehandlingstema(new Behandlingstema().withValue(BEHANDLINGSTEMA))
                        .withForsendelseInnsendt(LocalDateTime.now())
                        .withForsendelseMottatt(søknad.getMottattdato())
                        .withAvsender(new Person().withIdent(søker.fnr.getFnr()))
                        .withBruker(new Person().withIdent(søker.fnr.getFnr())))
                .withHoveddokument(hoveddokument(søknad, søker))
                .withVedleggListe(dokmotVedleggListe(søknad));
    }

    private Hoveddokument hoveddokument(Søknad søknad, no.nav.foreldrepenger.mottak.domain.felles.Person søker) {
        Dokumentinnhold hovedskjemaInnhold = new Dokumentinnhold()
                .withDokument(søknadGenerator.tilPdf(søknad, søker))
                .withArkivfiltype(new Arkivfiltyper().withValue(PDFA.name()))
                .withVariantformat(new Variantformater().withValue(ARKIV.name()));
        Stream<Dokumentinnhold> alternativeRepresentasjonerInnhold = Collections.singletonList(new Dokumentinnhold()
                .withDokument(søknadGenerator.tilXML(søknad, søker).getBytes())
                .withVariantformat(new Variantformater().withValue(ORIGINAL.name()))
                .withArkivfiltype(new Arkivfiltyper().withValue(XML.name()))).stream();

        return new Hoveddokument()
                .withDokumenttypeId(I000003.name())
                .withDokumentinnholdListe(
                        Stream.concat(Stream.of(hovedskjemaInnhold), alternativeRepresentasjonerInnhold)
                                .collect(toList()));
    }

    private static List<no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg> dokmotVedleggListe(Søknad søknad) {
        return Stream.concat(søknad.getPåkrevdeVedlegg().stream(), søknad.getFrivilligeVedlegg().stream())
                .map(DokmotEngangsstønadXMLKonvoluttGenerator::dokmotVedlegg).collect(toList());
    }

    private static no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg dokmotVedlegg(Vedlegg vedlegg) {

        return new no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg()
                .withBrukeroppgittTittel(vedlegg.getBeskrivelse())
                .withDokumenttypeId(vedlegg.getMetadata().getId())
                .withDokumentinnholdListe(new Dokumentinnhold()
                        .withVariantformat(new Variantformater().withValue(ARKIV.name()))
                        .withArkivfiltype(new Arkivfiltyper().withValue(Filtype.PDF.name()))
                        .withDokument(vedlegg.getVedlegg()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [søknadGenerator=" + søknadGenerator + "]";
    }

}
