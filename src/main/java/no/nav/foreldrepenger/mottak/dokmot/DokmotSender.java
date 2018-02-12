package no.nav.foreldrepenger.mottak.dokmot;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.foreldrepenger.mottak.dokmot.DokmotData.HovedskjemaData;
import no.nav.foreldrepenger.mottak.dokmot.DokmotData.VedleggData;
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
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Vedlegg;

public class DokmotSender {

    private Dokumentforsendelse dokumentForsendelse(DokmotData data) {
        return new Dokumentforsendelse()
                .withForsendelsesinformasjon(new Forsendelsesinformasjon()
                        .withForsendelseInnsendt(data.innsendtDato)
                        .withForsendelseMottatt(data.innsendtDato)
                        .withAvsender(new Person(data.fodselsnummer))
                        .withBruker(new Person(data.fodselsnummer))
                        .withTema(new Tema().withValue(data.tema))
                        .withBehandlingstema(new Behandlingstema().withValue(data.behandlingstema))
                        .withKanalreferanseId(data.behandlingsId)
                        .withMottakskanal(new Mottakskanaler().withValue("NAV_NO")))
                .withHoveddokument(hoveddokument(data.hovedskjema, data.aktorId, data.alternativRepresentasjonListe,
                        data.henvendelsetype))
                .withVedleggListe(dokmotVedleggListe(data.vedleggListe, data.aktorId));
    }

    private Hoveddokument hoveddokument(HovedskjemaData hovedskjema, String aktorId,
            List<DokmotData.AlternativRepresentasjon> alternativeRepresentasjoner, String henvendelsetype) {
        Dokumentinnhold hovedskjemaInnhold = new Dokumentinnhold()
                // .withDokument(fillager.hentFilForAktoer(hovedskjema.uuid, aktorId))
                .withVariantformat(new Variantformater().withValue(hovedskjema.variant.name()))
                .withArkivfiltype(new Arkivfiltyper().withValue(hovedskjema.filtype.name()));

        Stream<Dokumentinnhold> alternativeRepresentasjonerInnhold = Optional.ofNullable(alternativeRepresentasjoner)
                .map(List::stream).orElse(Stream.empty())
                .map(alternativRepresentasjon -> new Dokumentinnhold()
                        // .withDokument(fillager.hentFilForAktoer(alternativRepresentasjon.uuid, aktorId))
                        .withVariantformat(new Variantformater().withValue(alternativRepresentasjon.variant.name()))
                        .withArkivfiltype(new Arkivfiltyper().withValue(alternativRepresentasjon.filtype.name())));

        String skjemanummer = /* erEttersendelse(henvendelsetype) ? hovedskjema.skjemanummer + "-E" : */ hovedskjema.skjemanummer;

        return new Hoveddokument()
                .withDokumenttypeId(SkjemanummerTilDokumentTypeKode.dokumentTypeKode(skjemanummer))
                .withDokumentinnholdListe(
                        Stream.concat(Stream.of(hovedskjemaInnhold), alternativeRepresentasjonerInnhold)
                                .collect(Collectors.toList()));
    }

    private List<Vedlegg> dokmotVedleggListe(List<VedleggData> vedleggDataListe, String aktorId) {
        return vedleggDataListe.stream()
                .map(v -> dokmotVedleggFraHenvendelseData(v, aktorId))
                .collect(toList());
    }

    private Vedlegg dokmotVedleggFraHenvendelseData(VedleggData vedlegg, String aktorId) {
        byte[] bytes = null; // TODO fillager.hentFilForAktoer(vedlegg.uuid, aktorId);
        return new Vedlegg()
                .withBrukeroppgittTittel(vedlegg.brukerTittel)
                .withDokumenttypeId(SkjemanummerTilDokumentTypeKode.dokumentTypeKode(vedlegg.skjemanummer))
                .withDokumentinnholdListe(new Dokumentinnhold()
                        .withVariantformat(new Variantformater().withValue(vedlegg.variant.name()))
                        .withArkivfiltype(new Arkivfiltyper().withValue(vedlegg.filtype.name()))
                        .withDokument(bytes));
    }

}
