package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.util.stream.Collectors.joining;
import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.DEV;
import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.LOCAL;
import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.TEST;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000049;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000060;
import static no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper.SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.innsending.pdf.modell.FeltBlokk.felt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.opptjening.EgenNæring;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Frilans;
import no.nav.foreldrepenger.common.domain.felles.opptjening.NorskOrganisasjon;
import no.nav.foreldrepenger.common.domain.felles.opptjening.UtenlandskOrganisasjon;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.Tilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Frilanser;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.Blokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentPerson;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.FritekstBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.GruppeBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.ListeBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.MottattDato;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.TabellRad;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.TemaBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste.PdfGenerator;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@Profile({ DEV, LOCAL, TEST })
@Component
public class NySvangerskapspengerPdfGenerator implements MappablePdfGenerator {
    private final SøknadTextFormatter textFormatter;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DATEFMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String SVP_VEDLEGG_TILRETTELEGGING = "svp.vedlegg.tilrettelegging";
    private final ArbeidsInfo arbeidsforhold;
    private final PdfGenerator pdfGenerator;

    @Inject
    public NySvangerskapspengerPdfGenerator(SøknadTextFormatter textFormatter,
            no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo arbeidsforhold, PdfGenerator pdfGenerator) {
        this.textFormatter = textFormatter;
        this.arbeidsforhold = arbeidsforhold;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public byte[] generer(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        var bestilling = svpSøknadFra(søknad, søker);
        return pdfGenerator.generate(bestilling);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return SVANGERSKAPSPENGER;
    }

    private DokumentBestilling svpSøknadFra(Søknad søknad, Person søker) {
        return new DokumentBestilling(
                txt("svp.søknad"),
                personFra(søker),
                mottattDato(),
                lagInnhold(søknad));
    }

    private List<TemaBlokk> lagInnhold(Søknad søknad) {

        var stønad = (Svangerskapspenger) søknad.getYtelse();
        var aktiveArbeidsforhold = hentAktiveArbeidsforhold(stønad.getTermindato(), stønad.getFødselsdato());
        var medlemsskap = stønad.getMedlemsskap();
        // var annenForelder = stønad.getAnnenForelder();
        List<TemaBlokk> temaer = new ArrayList<>();

        temaer.add(omBarn(søknad, stønad));
        temaer.add(tilretteleggingsbehov(stønad, søknad.getVedlegg()));
        arbeidsforhold(aktiveArbeidsforhold).ifPresent(temaer::add);
        frilans(stønad.getOpptjening().getFrilans()).ifPresent(temaer::add);
        egenNæring(stønad.getOpptjening().getEgenNæring()).ifPresent(temaer::add);
//        grupper.add(egenNæring());
//        grupper.add(utenlandskeArbeidsforhold());
//        grupper.add(tilknytning(medlemsskap, stønad));

        return temaer;
    }

    private Optional<TemaBlokk> egenNæring(List<EgenNæring> egneNæringer) {
        if (egneNæringer.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(egneNæringer(egneNæringer))
                .map(l -> new TemaBlokk(txt("egennæring"), l));

    }

    private List<Blokk> egneNæringer(List<EgenNæring> egenNæring) {
        return egenNæring.stream()
                .map(this::egenNæringMapper)
                .collect(Collectors.toList());
    }

    private GruppeBlokk egenNæringMapper(EgenNæring næring) {
        var gruppe = GruppeBlokk.builder();
        List<Blokk> rader = new ArrayList<>();
        if (næring.getPeriode().tom() == null) {
            rader.add(new FritekstBlokk(txt("egennæringpågår", textFormatter.dato(næring.getPeriode().fom()))));
        } else {
            rader.add(new FritekstBlokk(
                    txt("egennæringavsluttet",
                            textFormatter.dato(næring.getPeriode().fom()),
                            textFormatter.dato(næring.getPeriode().tom()))));
        }
        if (næring instanceof NorskOrganisasjon) {
            NorskOrganisasjon org = NorskOrganisasjon.class.cast(næring);
            gruppe.medOverskrift(txt("virksomhetsnavn", org.getOrgName()));
            rader.add(rad(txt("orgnummer"), org.getOrgNummer().value()));
            rader.add(rad(txt("registrertiland"), textFormatter.countryName(CountryCode.NO)));
        }
        if (næring instanceof UtenlandskOrganisasjon) {
            UtenlandskOrganisasjon org = UtenlandskOrganisasjon.class.cast(næring);
            gruppe.medOverskrift(txt("virksomhetsnavn", org.getOrgName()));
            rader.add(rad(txt("registrertiland"), textFormatter.countryName(org.getRegistrertILand())));
        }
        rader.add(rad(txt("egennæringtyper", næring.getVedlegg().size() > 1 ? "r" : ""),
                safeStream(næring.getVirksomhetsTyper())
                        .map(vt -> textFormatter.capitalize(vt.name()))
                        .collect(joining(", "))));

        if (næring.getStillingsprosent() != null) {
            rader.add(rad(txt("stillingsprosent"), prosentFra(næring.getStillingsprosent())));
        }
        rader.add(rad(txt("nyligyrkesaktiv"), jaNei(næring.isErNyIArbeidslivet())));
        rader.add(rad(txt("varigendring"), jaNei(næring.isErVarigEndring())));
        Optional.ofNullable(næring.getBeskrivelseEndring())
                .map(b -> new FritekstBlokk(txt("egennæringbeskrivelseendring", b)))
                .ifPresent(rader::add);
        Optional.ofNullable(næring.getEndringsDato())
                .map(textFormatter::dato)
                .map(d -> rad(txt("egennæringendringsdato"), d))
                .ifPresent(rader::add);
        Optional.of(næring.getNæringsinntektBrutto())
                .filter(b -> b > 0L)
                .map(String::valueOf)
                .map(bi -> rad(txt("egennæringbruttoinntekt"), bi))
                .ifPresent(rader::add);

        if (næring.isErNyOpprettet()) {
            rader.add(rad(txt("nystartetvirksomhet"), jaNei(true)));
            Optional.ofNullable(næring.getOppstartsDato())
                    .map(textFormatter::dato)
                    .map(d -> rad(txt("egennæringoppstartsdato"), d))
                    .ifPresent(rader::add);
        }

        return gruppe.medTabellRader(rader).build();
    }

    private Optional<TemaBlokk> frilans(Frilans frilans) {
        if (frilans == null) {
            return Optional.empty();
        }
        List<Blokk> rader = new ArrayList<>();
        if (frilans.getPeriode().tom() == null) {
            rader.add(new FritekstBlokk(txt("frilanspågår", textFormatter.dato(frilans.getPeriode().fom()))));
        } else {
            rader.add(new FritekstBlokk(txt("frilansavsluttet", textFormatter.dato(frilans.getPeriode().fom()),
                    textFormatter.dato(frilans.getPeriode().tom()))));
        }
        rader.add(felt(txt("fosterhjem"), jaNei(frilans.isHarInntektFraFosterhjem())));
        rader.add(felt(txt("nyoppstartet"), jaNei(frilans.isNyOppstartet())));
        if (!frilans.getFrilansOppdrag().isEmpty()) {
            frilans.getFrilansOppdrag().stream()
                    .map(o -> rad(o.oppdragsgiver(), textFormatter.enkelPeriode(o.periode())))
                    .forEach(rader::add);
        } else {
            rader.add(felt(txt("oppdrag"), "Nei"));
        }

        return Optional.of(TemaBlokk.builder()
                .medOverskrift(txt("frilans"))
                .medUnderBlokker(rader)
                .build());
    }

    private String jaNei(boolean value) {
        return textFormatter.yesNo(value);
    }

    private Optional<TemaBlokk> arbeidsforhold(List<EnkeltArbeidsforhold> aktiveArbeidsforhold) {
        if (aktiveArbeidsforhold.isEmpty()) {
            return Optional.empty();
        }
        List<TabellRad> rader = new ArrayList<>();
        for (var arbeidsforhold : sorterArbeidsforhold(aktiveArbeidsforhold)) {
            var builder = TabellRad.builder()
                    .medVenstreTekst(arbeidsforhold.getArbeidsgiverNavn())
                    .medHøyreTekst(arbeidsforhold.getFrom() + " - " + arbeidsforhold.getTo());
            if (arbeidsforhold.getStillingsprosent() != null) {
                builder
                        .medUnderBlokker(List.of(rad(txt("stillingsprosent"),
                                prosentFra(arbeidsforhold.getStillingsprosent()))));
            }
            rader.add(builder.build());
        }
        return Optional.of(TemaBlokk.builder()
                .medOverskrift(txt("arbeidsforhold"))
                .medUnderBlokker(rader)
                .build());
    }

    private static List<EnkeltArbeidsforhold> sorterArbeidsforhold(List<EnkeltArbeidsforhold> arbeidsforhold) {
        final List<EnkeltArbeidsforhold> mutableList = new ArrayList<>(arbeidsforhold);
        Collections.sort(mutableList, (o1, o2) -> {
            if (o1.getFrom() != null && o2.getFrom() != null) {
                return o1.getFrom().compareTo(o2.getFrom());
            }
            return 0;
        });
        return mutableList;
    }

    private TemaBlokk tilretteleggingsbehov(Svangerskapspenger stønad,
            List<Vedlegg> vedlegg) {
        List<GruppeBlokk> tabeller = new ArrayList<>();

        perArbeidsforholdFra(stønad.getTilrettelegging())
                .forEach((arbeidsgiver, tiltak) -> tabeller.add(håndter(arbeidsgiver, tiltak, vedlegg)));

        return TemaBlokk.builder()
                .medOverskrift(txt("tilrettelegging"))
                .medUnderBlokker(tabeller)
                .build();
    }

    private GruppeBlokk håndter(Arbeidsforhold arbeidsgiver,
            List<Tilrettelegging> tiltak,
            List<Vedlegg> vedlegg) {
        var builder = GruppeBlokk.builder();
        List<Blokk> rader = new ArrayList<>();
        rader.add(behovFra(tiltak.stream().findAny().orElseThrow()));
        if (arbeidsgiver instanceof Virksomhet) {
            builder.medOverskrift(virksomhetsnavn(((Virksomhet) arbeidsgiver).getOrgnr()));
            tiltak.stream().map(this::map).forEach(rader::addAll);
        } else if (arbeidsgiver instanceof PrivatArbeidsgiver) {
            builder.medOverskrift(txt("svp.privatarbeidsgiver"));
            tiltak.stream().map(this::map).forEach(rader::addAll);
        } else if (arbeidsgiver instanceof SelvstendigNæringsdrivende) {
            builder.medOverskrift(txt("svp.selvstendig"));
            tiltak.stream().map(this::map).forEach(rader::addAll);
        } else {
            var frilans = (Frilanser) arbeidsgiver;
            builder.medOverskrift(txt("svp.frilans"));
            tiltak.stream().map(this::map).forEach(rader::addAll);
            rader.addAll(tiltakOgRisiko(frilans));
        }

        tiltak.stream()
                .map(Tilrettelegging::getVedlegg)
                .findAny()
                .map(ref -> lagVedlegg(vedlegg, ref, SVP_VEDLEGG_TILRETTELEGGING))
                .ifPresent(rader::add);

        return builder.medTabellRader(rader).build();
    }

    private ListeBlokk lagVedlegg(List<Vedlegg> vedlegg, List<String> vedleggRefs, String svpVedleggTilrettelegging) {
        List<String> vedleggPunkter = new ArrayList<>();
        for (String ref : vedleggRefs) {
            var detaljer = vedlegg.stream()
                    .filter(s -> ref.equals(s.getId()))
                    .findFirst();
            if (detaljer.isPresent()) {
                String beskrivelse = vedleggsBeskrivelse(svpVedleggTilrettelegging, detaljer.get());
                vedleggPunkter.add(txt("vedlegg2", beskrivelse, detaljer.get().getInnsendingsType().name()));
            } else {
                vedleggPunkter.add("Vedlegg");
            }
        }

        return new ListeBlokk(txt("vedlegg1"), vedleggPunkter);
    }

    private String vedleggsBeskrivelse(String key, Vedlegg vedlegg) {
        return erAnnenDokumentType(vedlegg) ? txt(key) : vedlegg.getBeskrivelse();
    }

    private static boolean erAnnenDokumentType(Vedlegg vedlegg) {
        return vedlegg.getDokumentType().equals(I000060) || vedlegg.getDokumentType().equals(I000049);
    }

    private List<FritekstBlokk> tiltakOgRisiko(Frilanser frilans) {
        return List.of(
                new FritekstBlokk(txt("svp.risikofaktorer", frilans.getRisikoFaktorer())),
                new FritekstBlokk(txt("svp.tiltak", frilans.getTilretteleggingstiltak())));
    }

    private List<TabellRad> map(Tilrettelegging tilrettelegging) {
        if (tilrettelegging instanceof HelTilrettelegging) {
            return List.of(rad(txt("svp.tilretteleggingfra"),
                    DATEFMT.format(((HelTilrettelegging) tilrettelegging).getTilrettelagtArbeidFom())));
        } else if (tilrettelegging instanceof DelvisTilrettelegging) {
            var periode = (DelvisTilrettelegging) tilrettelegging;
            var tilrettelagtArbeidFom = DATEFMT.format(periode.getTilrettelagtArbeidFom());
            var stillingsprosent = prosentFra(periode.getStillingsprosent());
            return List.of(
                    rad(txt("svp.delvistilrettelegging"), tilrettelagtArbeidFom),
                    rad(txt("svp.stillingsprosent"), stillingsprosent));
        } else {
            var ingenTilrettelegging = (IngenTilrettelegging) tilrettelegging;
            var ikkeArbeidDato = DATEFMT.format(ingenTilrettelegging.getSlutteArbeidFom());
            var rad = TabellRad.builder()
                    .medVenstreTekst(txt("svp.sluttearbeid"))
                    .medHøyreTekst(ikkeArbeidDato)
                    .build();
            return List.of(rad);
        }
    }

    private static String prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::getProsent)
                .map(String::valueOf)
                .orElse("0");
    }

    private TabellRad behovFra(Tilrettelegging tilfeldigPeriode) {
        return rad(txt("svp.behovfra", ""),
                DATEFMT.format(tilfeldigPeriode.getBehovForTilretteleggingFom()));
    }

    private TabellRad rad(String txt, String format) {
        return TabellRad.builder()
                .medVenstreTekst(txt)
                .medHøyreTekst(format)
                .build();
    }

    private String virksomhetsnavn(Orgnummer orgnr) {
        return safeStream(arbeidsforhold.hentArbeidsforhold())
                .filter(af -> af.getArbeidsgiverId().equals(orgnr.value()))
                .findFirst()
                .map(EnkeltArbeidsforhold::getArbeidsgiverNavn)
                .orElse(txt("arbeidsgiverIkkeFunnet", orgnr));
    }

    private static Map<Arbeidsforhold, List<Tilrettelegging>> perArbeidsforholdFra(List<Tilrettelegging> tilretteleggingsPerioder) {
        Map<Arbeidsforhold, List<Tilrettelegging>> tilretteleggingByArbeidsforhold = new HashMap<>();
        tilretteleggingsPerioder.forEach(tp -> tilretteleggingByArbeidsforhold
                .computeIfAbsent(tp.getArbeidsforhold(), key -> new ArrayList<>())
                .add(tp));
        return tilretteleggingByArbeidsforhold;
    }

    private List<EnkeltArbeidsforhold> hentAktiveArbeidsforhold(
            LocalDate termindato,
            LocalDate fødselsdato) {
        LocalDate relasjonsDato = fødselsdato != null ? fødselsdato : termindato;
        return safeStream(arbeidsforhold.hentArbeidsforhold())
                .filter(a -> a.getTo().isEmpty() || (a.getTo().isPresent() && a.getTo().get().isAfter(relasjonsDato)))
                .toList();
    }

    private TemaBlokk omBarn(Søknad søknad, Svangerskapspenger stønad) {
        FritekstBlokk terminFødsel;
        if (stønad.getFødselsdato() != null) {
            terminFødsel = new FritekstBlokk(txt("svp.omfødsel", stønad.getFødselsdato(), DATEFMT.format(stønad.getTermindato())));
        } else {
            terminFødsel = new FritekstBlokk(txt("svp.termindato", DATEFMT.format(stønad.getTermindato())));
        }
        return TemaBlokk.builder()
                .medOverskrift(txt("ombarn"))
                .medUnderBlokker(List.of(terminFødsel))
                .build();
    }

    private String txt(String gjelder, Object... values) {
        return textFormatter.fromMessageSource(gjelder, values);
    }

    private String txt(String key) {
        return textFormatter.fromMessageSource(key);
    }

    private DokumentPerson personFra(Person person) {
        var navn = textFormatter.sammensattNavn(new Navn(person.getFornavn(),
                person.getMellomnavn(), person.getEtternavn()));
        return DokumentPerson.builder().navn(navn).id(person.fnr().getFnr()).build();
    }

    private MottattDato mottattDato() {
        return new MottattDato(txt("mottattid"), FMT.format(LocalDateTime.now()));
    }

}
