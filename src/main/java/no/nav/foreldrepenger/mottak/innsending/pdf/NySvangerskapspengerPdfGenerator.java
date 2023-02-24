package no.nav.foreldrepenger.mottak.innsending.pdf;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.VedleggReferanse;
import no.nav.foreldrepenger.common.domain.felles.opptjening.EgenNæring;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Frilans;
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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
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

import static java.util.stream.Collectors.joining;
import static no.nav.boot.conditionals.EnvUtil.DEV;
import static no.nav.boot.conditionals.EnvUtil.LOCAL;
import static no.nav.boot.conditionals.EnvUtil.TEST;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000049;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000060;
import static no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper.SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.innsending.pdf.modell.FeltBlokk.felt;

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
        var aktiveArbeidsforhold = hentAktiveArbeidsforhold(stønad.termindato(), stønad.fødselsdato());
        var medlemsskap = stønad.medlemsskap();
        // var annenForelder = stønad.getAnnenForelder();
        List<TemaBlokk> temaer = new ArrayList<>();

        temaer.add(omBarn(søknad, stønad));
        temaer.add(tilretteleggingsbehov(stønad, søknad.getVedlegg()));
        arbeidsforhold(aktiveArbeidsforhold).ifPresent(temaer::add);
        frilans(stønad.opptjening().frilans()).ifPresent(temaer::add);
        egenNæring(stønad.opptjening().egenNæring()).ifPresent(temaer::add);
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
        if (næring.periode().tom() == null) {
            rader.add(new FritekstBlokk(txt("egennæringpågår", textFormatter.dato(næring.periode().fom()))));
        } else {
            rader.add(new FritekstBlokk(
                    txt("egennæringavsluttet",
                            textFormatter.dato(næring.periode().fom()),
                            textFormatter.dato(næring.periode().tom()))));
        }
        if (CountryCode.NO.equals(næring.registrertILand())) {
            gruppe.overskrift(txt("virksomhetsnavn", næring.orgName()));
            rader.add(rad(txt("orgnummer"), næring.orgNummer().value()));
            rader.add(rad(txt("registrertiland"), textFormatter.countryName(CountryCode.NO)));
        } else {
            gruppe.overskrift(txt("virksomhetsnavn", næring.orgName()));
            rader.add(rad(txt("registrertiland"), textFormatter.countryName(næring.registrertILand())));
        }
        rader.add(rad(txt("egennæringtyper", næring.vedlegg().size() > 1 ? "r" : ""),
                safeStream(næring.virksomhetsTyper())
                        .map(vt -> textFormatter.capitalize(vt.name()))
                        .collect(joining(", "))));

        if (næring.stillingsprosent() != null) {
            rader.add(rad(txt("stillingsprosent"), prosentFra(næring.stillingsprosent())));
        }
        rader.add(rad(txt("nyligyrkesaktiv"), jaNei(næring.erNyIArbeidslivet())));
        rader.add(rad(txt("varigendring"), jaNei(næring.erVarigEndring())));
        Optional.ofNullable(næring.beskrivelseEndring())
                .map(b -> new FritekstBlokk(txt("egennæringbeskrivelseendring", b)))
                .ifPresent(rader::add);
        Optional.ofNullable(næring.endringsDato())
                .map(textFormatter::dato)
                .map(d -> rad(txt("egennæringendringsdato"), d))
                .ifPresent(rader::add);
        Optional.of(næring.næringsinntektBrutto())
                .filter(b -> b > 0L)
                .map(String::valueOf)
                .map(bi -> rad(txt("egennæringbruttoinntekt"), bi))
                .ifPresent(rader::add);

        if (næring.erNyOpprettet()) {
            rader.add(rad(txt("nystartetvirksomhet"), jaNei(true)));
            Optional.ofNullable(næring.oppstartsDato())
                    .map(textFormatter::dato)
                    .map(d -> rad(txt("egennæringoppstartsdato"), d))
                    .ifPresent(rader::add);
        }

        return gruppe.tabellRader(rader).build();
    }

    private Optional<TemaBlokk> frilans(Frilans frilans) {
        if (frilans == null) {
            return Optional.empty();
        }
        List<Blokk> rader = new ArrayList<>();
        if (frilans.jobberFremdelesSomFrilans()) {
            rader.add(new FritekstBlokk(txt("frilanspågår", textFormatter.dato(frilans.periode().fom()))));
        } else {
            rader.add(new FritekstBlokk(txt("frilansavsluttet", textFormatter.dato(frilans.periode().fom()))));
        }
        rader.add(felt(txt("fosterhjem"), jaNei(frilans.harInntektFraFosterhjem())));
        rader.add(felt(txt("nyoppstartet"), jaNei(frilans.nyOppstartet())));
        if (!frilans.frilansOppdrag().isEmpty()) {
            frilans.frilansOppdrag().stream()
                    .map(o -> rad(o.oppdragsgiver(), textFormatter.enkelPeriode(o.periode())))
                    .forEach(rader::add);
        } else {
            rader.add(felt(txt("oppdrag"), "Nei"));
        }

        return Optional.of(new TemaBlokk(txt("frilans"), rader));
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
            var stillingsprosent = arbeidsforhold.stillingsprosent() != null ?
                List.of(rad(txt("stillingsprosent"), prosentFra(arbeidsforhold.stillingsprosent()))) : null;
            var tabellRad = new TabellRad(
                arbeidsforhold.arbeidsgiverNavn(),
                arbeidsforhold.from() + " - " + arbeidsforhold.to(),
                stillingsprosent);
            rader.add(tabellRad);
        }
        return Optional.of(new TemaBlokk(txt("arbeidsforhold"), rader));
    }

    private static List<EnkeltArbeidsforhold> sorterArbeidsforhold(List<EnkeltArbeidsforhold> arbeidsforhold) {
        final List<EnkeltArbeidsforhold> mutableList = new ArrayList<>(arbeidsforhold);
        Collections.sort(mutableList, (o1, o2) -> {
            if (o1.from() != null && o2.from() != null) {
                return o1.from().compareTo(o2.from());
            }
            return 0;
        });
        return mutableList;
    }

    private TemaBlokk tilretteleggingsbehov(Svangerskapspenger stønad,
            List<Vedlegg> vedlegg) {
        List<GruppeBlokk> tabeller = new ArrayList<>();

        perArbeidsforholdFra(stønad.tilrettelegging())
                .forEach((arbeidsgiver, tiltak) -> tabeller.add(håndter(arbeidsgiver, tiltak, vedlegg)));

        return new TemaBlokk(txt("tilrettelegging"), tabeller);
    }

    private GruppeBlokk håndter(Arbeidsforhold arbeidsgiver,
            List<Tilrettelegging> tiltak,
            List<Vedlegg> vedlegg) {
        var builder = GruppeBlokk.builder();
        List<Blokk> rader = new ArrayList<>();
        rader.add(behovFra(tiltak.stream().findAny().orElseThrow()));
        if (arbeidsgiver instanceof Virksomhet) {
            builder.overskrift(virksomhetsnavn(((Virksomhet) arbeidsgiver).orgnr()));
            tiltak.stream().map(this::map).forEach(rader::addAll);
        } else if (arbeidsgiver instanceof PrivatArbeidsgiver) {
            builder.overskrift(txt("svp.privatarbeidsgiver"));
            tiltak.stream().map(this::map).forEach(rader::addAll);
        } else if (arbeidsgiver instanceof SelvstendigNæringsdrivende) {
            builder.overskrift(txt("svp.selvstendig"));
            tiltak.stream().map(this::map).forEach(rader::addAll);
        } else {
            var frilans = (Frilanser) arbeidsgiver;
            builder.overskrift(txt("svp.frilans"));
            tiltak.stream().map(this::map).forEach(rader::addAll);
            rader.addAll(tiltakOgRisiko(frilans));
        }

        tiltak.stream()
                .map(Tilrettelegging::getVedlegg)
                .findAny()
                .map(ref -> lagVedlegg(vedlegg, ref, SVP_VEDLEGG_TILRETTELEGGING))
                .ifPresent(rader::add);

        return builder.tabellRader(rader).build();
    }

    private ListeBlokk lagVedlegg(List<Vedlegg> vedlegg, List<VedleggReferanse> vedleggRefs, String svpVedleggTilrettelegging) {
        List<String> vedleggPunkter = new ArrayList<>();
        for (var ref : vedleggRefs) {
            var detaljer = vedlegg.stream()
                    .filter(s -> ref.referanse().equals(s.getId()))
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
                new FritekstBlokk(txt("svp.risikofaktorer", frilans.risikoFaktorer())),
                new FritekstBlokk(txt("svp.tiltak", frilans.tilretteleggingstiltak())));
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
            var rad = new TabellRad(txt("svp.sluttearbeid"), ikkeArbeidDato, null);
            return List.of(rad);
        }
    }

    private static String prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::prosent)
                .map(String::valueOf)
                .orElse("0");
    }

    private TabellRad behovFra(Tilrettelegging tilfeldigPeriode) {
        return rad(txt("svp.behovfra", ""),
                DATEFMT.format(tilfeldigPeriode.getBehovForTilretteleggingFom()));
    }

    private TabellRad rad(String txt, String format) {
        return new TabellRad(txt, format, null);
    }

    private String virksomhetsnavn(Orgnummer orgnr) {
        return safeStream(arbeidsforhold.hentArbeidsforhold())
                .filter(af -> af.arbeidsgiverId().equals(orgnr.value()))
                .findFirst()
                .map(EnkeltArbeidsforhold::arbeidsgiverNavn)
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
                .filter(a -> a.to().isEmpty() || (a.to().isPresent() && a.to().get().isAfter(relasjonsDato)))
                .toList();
    }

    private TemaBlokk omBarn(Søknad søknad, Svangerskapspenger stønad) {
        FritekstBlokk terminFødsel;
        if (stønad.fødselsdato() != null) {
            terminFødsel = new FritekstBlokk(txt("svp.omfødsel", stønad.fødselsdato(), DATEFMT.format(stønad.termindato())));
        } else {
            terminFødsel = new FritekstBlokk(txt("svp.termindato", DATEFMT.format(stønad.termindato())));
        }
        return new TemaBlokk(txt("ombarn"), List.of(terminFødsel));
    }

    private String txt(String gjelder, Object... values) {
        return textFormatter.fromMessageSource(gjelder, values);
    }

    private String txt(String key) {
        return textFormatter.fromMessageSource(key);
    }

    private DokumentPerson personFra(Person person) {
        var navn = textFormatter.sammensattNavn(person);
        return new DokumentPerson(
            person.fnr().value(),
            null,
            navn,
            null,
            null,
            null
        );
    }

    private MottattDato mottattDato() {
        return new MottattDato(txt("mottattid"), FMT.format(LocalDateTime.now()));
    }

}
