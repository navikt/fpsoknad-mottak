package no.nav.foreldrepenger.mottak.innsending.mappers;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.andreOpptjeningerFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.egneNæringerFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.frilansFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.landFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.språkFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.utenlandskeArbeidsforholdFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.vedleggFra;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V3;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.GradertUttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.OppholdsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Oppholdsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.OverføringsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UttaksPeriode;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.foreldrepenger.mottak.util.jaxb.FPV3JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v3.Endringssoeknad;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderMedNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderUtenNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Foedsel;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.OppholdUtlandet;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Periode;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Rettigheter;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.SoekersRelasjonTilBarnet;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Termin;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.UkjentForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Dekningsgrad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Brukerroller;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Dekningsgrader;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.MorsAktivitetsTyper;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Omsorgsovertakelseaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Oppholdsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Overfoeringsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Utsettelsesaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Uttaksperiodetyper;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Arbeidsgiver;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Gradering;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Oppholdsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Overfoeringsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Person;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Utsettelsesperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Uttaksperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Virksomhet;
import no.nav.vedtak.felles.xml.soeknad.v3.OmYtelse;
import no.nav.vedtak.felles.xml.soeknad.v3.Soeknad;

@Component
public class V3ForeldrepengerDomainMapper implements DomainMapper {

    private static final Versjon HÅNDTERT_VERSJON = V3;

    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(HÅNDTERT_VERSJON, ENDRING_FORELDREPENGER,
            INITIELL_FORELDREPENGER);

    private static final FPV3JAXBUtil JAXB = new FPV3JAXBUtil();
    private static final Logger LOG = LoggerFactory.getLogger(V3ForeldrepengerDomainMapper.class);

    private static final no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.ObjectFactory FP_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.felles.v3.ObjectFactory FELLES_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.felles.v3.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.v3.ObjectFactory SØKNAD_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.v3.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.uttak.v3.ObjectFactory UTTAK_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.uttak.v3.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v3.ObjectFactory ENDRING_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v3.ObjectFactory();

    private final Oppslag oppslag;

    public V3ForeldrepengerDomainMapper(Oppslag oppslag) {
        this.oppslag = oppslag;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return EGENSKAPER;
    }

    @Override
    public String tilXML(Søknad søknad, AktorId søker, SøknadEgenskap egenskap) {
        return JAXB.marshal(SØKNAD_FACTORY_V3.createSoeknad(tilModell(søknad, søker)));
    }

    @Override
    public String tilXML(Endringssøknad endringssøknad, AktorId søker, SøknadEgenskap egenskap) {
        return JAXB.marshal(SØKNAD_FACTORY_V3.createSoeknad(tilModell(endringssøknad, søker)));
    }

    private Soeknad tilModell(Endringssøknad endringsøknad, AktorId søker) {
        LOG.debug(CONFIDENTIAL, "Genererer endringssøknad XML fra {}", endringsøknad);
        return new Soeknad()
                .withSprakvalg(språkFra(endringsøknad.getSøker()))
                .withMottattDato(endringsøknad.getMottattdato().toLocalDate())
                .withSoeker(søkerFra(søker, endringsøknad.getSøker()))
                .withAndreVedlegg(vedleggFra(endringsøknad.getFrivilligeVedlegg()))
                .withPaakrevdeVedlegg(vedleggFra(endringsøknad.getPåkrevdeVedlegg()))
                .withOmYtelse(ytelseFra(endringsøknad));
    }

    private OmYtelse ytelseFra(Endringssøknad endringssøknad) {
        return new OmYtelse().withAny(endringssøknadFra(endringssøknad));
    }

    private JAXBElement<Endringssoeknad> endringssøknadFra(Endringssøknad endringssøknad) {
        return ENDRING_FACTORY_V3.createEndringssoeknad(new Endringssoeknad()
                .withFordeling(fordelingFra(endringssøknad))
                .withSaksnummer(endringssøknad.getSaksnr()));
    }

    public Soeknad tilModell(Søknad søknad, AktorId søker) {
        return new Soeknad()
                .withSprakvalg(språkFra(søknad.getSøker()))
                .withAndreVedlegg(vedleggFra(søknad.getFrivilligeVedlegg()))
                .withPaakrevdeVedlegg(vedleggFra(søknad.getPåkrevdeVedlegg()))
                .withSoeker(søkerFra(søker, søknad.getSøker()))
                .withOmYtelse(ytelseFra(søknad))
                .withMottattDato(søknad.getMottattdato().toLocalDate())
                .withBegrunnelseForSenSoeknad(søknad.getBegrunnelseForSenSøknad())
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger());
    }

    private OmYtelse ytelseFra(Søknad søknad) {
        no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger ytelse = no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.class
                .cast(søknad.getYtelse());
        return new OmYtelse().withAny(JAXB.marshalToElement(foreldrePengerFra(ytelse)));
    }

    private JAXBElement<Foreldrepenger> foreldrePengerFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger ytelse) {
        return FP_FACTORY_V3.createForeldrepenger(new Foreldrepenger()
                .withDekningsgrad(dekningsgradFra(ytelse.getDekningsgrad()))
                .withMedlemskap(medlemsskapFra(ytelse.getMedlemsskap(), ytelse.getRelasjonTilBarn()))
                .withOpptjening(opptjeningFra(ytelse.getOpptjening()))
                .withFordeling(fordelingFra(ytelse.getFordeling()))
                .withRettigheter(
                        rettigheterFra(ytelse.getRettigheter(), erAnnenForelderUkjent(ytelse.getAnnenForelder())))
                .withAnnenForelder(annenForelderFra(ytelse.getAnnenForelder()))
                .withRelasjonTilBarnet(relasjonFra(ytelse.getRelasjonTilBarn())));
    }

    private static Opptjening opptjeningFra(
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening opptjening) {
        return new Opptjening()
                .withUtenlandskArbeidsforhold(utenlandskeArbeidsforholdFra(opptjening.getUtenlandskArbeidsforhold()))
                .withFrilans(frilansFra(opptjening.getFrilans()))
                .withEgenNaering(egneNæringerFra(opptjening.getEgenNæring()))
                .withAnnenOpptjening(andreOpptjeningerFra(opptjening.getAnnenOpptjening()));
    }

    private Fordeling fordelingFra(Endringssøknad endringssøknad) {
        no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger ytelse = no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.class
                .cast(endringssøknad.getYtelse());
        return fordelingFra(ytelse.getFordeling());

    }

    private static boolean erAnnenForelderUkjent(
            no.nav.foreldrepenger.mottak.domain.felles.annenforelder.AnnenForelder annenForelder) {
        return annenForelder != null
                && annenForelder instanceof no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UkjentForelder;
    }

    private static Dekningsgrad dekningsgradFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad dekningsgrad) {
        return Optional.ofNullable(dekningsgrad)
                .map(s -> dekningsgradFra(s.kode()))
                .map(s -> new Dekningsgrad().withDekningsgrad(s))
                .orElse(null);
    }

    private static Dekningsgrader dekningsgradFra(String kode) {
        Dekningsgrader dekningsgrad = new Dekningsgrader().withKode(kode);
        return dekningsgrad.withKodeverk(dekningsgrad.getKodeverk());
    }

    private static Medlemskap medlemsskapFra(Medlemsskap ms, RelasjonTilBarn relasjon) {
        if (ms != null) {
            return new Medlemskap()
                    .withOppholdUtlandet(oppholdUtlandetFra(ms))
                    .withINorgeVedFoedselstidspunkt(ms.varINorge(relasjon.relasjonsDato()))
                    .withBoddINorgeSiste12Mnd(oppholdINorgeSiste12(ms))
                    .withBorINorgeNeste12Mnd(oppholdINorgeNeste12(ms));
        }
        return null;
    }

    private static boolean oppholdINorgeSiste12(Medlemsskap ms) {
        return ms.getTidligereOppholdsInfo().getUtenlandsOpphold().isEmpty();
    }

    private static boolean oppholdINorgeNeste12(Medlemsskap ms) {
        return ms.getFramtidigOppholdsInfo().getUtenlandsOpphold().isEmpty();
    }

    private static List<OppholdUtlandet> oppholdUtlandetFra(Medlemsskap ms) {
        return ms.utenlandsOpphold()
                .stream()
                .map(V3ForeldrepengerDomainMapper::utenlandOppholdFra)
                .collect(toList());
    }

    private static OppholdUtlandet utenlandOppholdFra(Utenlandsopphold opphold) {
        return opphold == null ? null
                : new OppholdUtlandet()
                        .withPeriode(new Periode()
                                .withFom(opphold.getFom())
                                .withTom(opphold.getTom()))
                        .withLand(landFra(opphold.getLand()));
    }

    private Fordeling fordelingFra(no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Fordeling fordeling) {
        if (fordeling == null) {
            return null;
        }
        return new Fordeling()
                .withPerioder(perioderFra(fordeling.getPerioder()))
                .withOenskerKvoteOverfoert(valgfriOverføringsÅrsakFra(fordeling.getØnskerKvoteOverført()))
                .withAnnenForelderErInformert(fordeling.isErAnnenForelderInformert());
    }

    private List<no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg> perioderFra(
            List<LukketPeriodeMedVedlegg> perioder) {
        return safeStream(perioder)
                .map(this::lukkerPeriodeFra)
                .collect(toList());

    }

    private static List<JAXBElement<Object>> lukketPeriodeVedleggFra(List<String> vedlegg) {
        return vedlegg.stream()
                .map(s -> UTTAK_FACTORY_V3.createLukketPeriodeMedVedleggVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg lukkerPeriodeFra(
            LukketPeriodeMedVedlegg periode) {
        if (periode instanceof OverføringsPeriode) {
            OverføringsPeriode overføringsPeriode = OverføringsPeriode.class.cast(periode);
            return new Overfoeringsperiode()
                    .withFom(overføringsPeriode.getFom())
                    .withTom(overføringsPeriode.getTom())
                    .withOverfoeringAv(uttaksperiodeTypeFra(overføringsPeriode.getUttaksperiodeType()))
                    .withAarsak(påkrevdOverføringsÅrsakFra(overføringsPeriode.getÅrsak()))
                    .withVedlegg(lukketPeriodeVedleggFra(overføringsPeriode.getVedlegg()));

        }
        if (periode instanceof OppholdsPeriode) {
            OppholdsPeriode oppholdsPeriode = OppholdsPeriode.class.cast(periode);
            return new Oppholdsperiode()
                    .withFom(oppholdsPeriode.getFom())
                    .withTom(oppholdsPeriode.getTom())
                    .withAarsak(oppholdsÅrsakFra(oppholdsPeriode.getÅrsak()))
                    .withVedlegg(lukketPeriodeVedleggFra(oppholdsPeriode.getVedlegg()));
        }
        if (periode instanceof UtsettelsesPeriode) {
            UtsettelsesPeriode utsettelsesPeriode = UtsettelsesPeriode.class.cast(periode);
            return new Utsettelsesperiode()
                    .withFom(utsettelsesPeriode.getFom())
                    .withTom(utsettelsesPeriode.getTom())
                    .withErArbeidstaker(utsettelsesPeriode.isErArbeidstaker())
                    .withMorsAktivitetIPerioden(morsAktivitetFra(utsettelsesPeriode.getMorsAktivitetsType()))
                    .withUtsettelseAv(uttaksperiodeTypeFra(utsettelsesPeriode.getUttaksperiodeType(), true))
                    .withAarsak(utsettelsesÅrsakFra(utsettelsesPeriode.getÅrsak()))
                    .withVedlegg(lukketPeriodeVedleggFra(utsettelsesPeriode.getVedlegg()));
        }
        if (periode instanceof GradertUttaksPeriode) {
            GradertUttaksPeriode gradertPeriode = GradertUttaksPeriode.class.cast(periode);
            Gradering gradering = new Gradering()
                    .withFom(gradertPeriode.getFom())
                    .withTom(gradertPeriode.getTom())
                    .withType(uttaksperiodeTypeFra(gradertPeriode.getUttaksperiodeType()))
                    .withOenskerSamtidigUttak(gradertPeriode.isØnskerSamtidigUttak())
                    .withMorsAktivitetIPerioden(morsAktivitetFra(gradertPeriode.getMorsAktivitetsType()))
                    .withOenskerFlerbarnsdager(gradertPeriode.isØnskerFlerbarnsdager())
                    .withErArbeidstaker(gradertPeriode.isErArbeidstaker())
                    .withArbeidtidProsent(gradertPeriode.getArbeidstidProsent())
                    .withArbeidsgiver(arbeidsGiverFra(gradertPeriode.getVirksomhetsnummer()))
                    .withArbeidsforholdSomSkalGraderes(gradertPeriode.isArbeidsForholdSomskalGraderes())
                    .withVedlegg(lukketPeriodeVedleggFra(gradertPeriode.getVedlegg()));
            return gradertPeriode.isØnskerSamtidigUttak()
                    ? gradering.withSamtidigUttakProsent(gradertPeriode.getSamtidigUttakProsent())
                    : gradering;

        }
        if (periode instanceof UttaksPeriode) {
            UttaksPeriode uttaksPeriode = UttaksPeriode.class.cast(periode);
            return new Uttaksperiode()
                    .withFom(uttaksPeriode.getFom())
                    .withTom(uttaksPeriode.getTom())
                    .withSamtidigUttakProsent(uttaksPeriode.getSamtidigUttakProsent())
                    .withOenskerFlerbarnsdager(uttaksPeriode.isØnskerFlerbarnsdager())
                    .withType(uttaksperiodeTypeFra(uttaksPeriode.getUttaksperiodeType()))
                    .withOenskerSamtidigUttak(uttaksPeriode.isØnskerSamtidigUttak())
                    .withMorsAktivitetIPerioden(morsAktivitetFra(uttaksPeriode.getMorsAktivitetsType()))
                    .withVedlegg(lukketPeriodeVedleggFra(uttaksPeriode.getVedlegg()));
        }
        throw new UnexpectedInputException("Vil aldri skje");
    }

    private static Arbeidsgiver arbeidsGiverFra(List<String> arbeidsgiver) {
        if (CollectionUtils.isEmpty(arbeidsgiver)) {
            return null;
        }
        String id = arbeidsgiver.get(0);
        switch (id.length()) {
        case 11:
            return new Person()
                    .withIdentifikator(id);
        case 9:
            return new Virksomhet()
                    .withIdentifikator(id);
        default:
            throw new UnexpectedInputException("Ugyldig lengde " + id.length() + " for arbeidsgiver");
        }
    }

    private static Uttaksperiodetyper uttaksperiodeTypeFra(StønadskontoType type) {
        return uttaksperiodeTypeFra(type, false);
    }

    private static Uttaksperiodetyper uttaksperiodeTypeFra(StønadskontoType type, boolean optional) {
        if (optional) {
            return Optional.ofNullable(type)
                    .map(s -> uttaksperiodeTypeFra(s.name()))
                    .orElse(null);
        }
        return Optional.ofNullable(type)
                .map(s -> uttaksperiodeTypeFra(s.name()))
                .orElseThrow(() -> new UnexpectedInputException("Stønadskontotype må være satt"));
    }

    private static Uttaksperiodetyper uttaksperiodeTypeFra(String type) {
        Uttaksperiodetyper periodeType = new Uttaksperiodetyper().withKode(type);
        return periodeType.withKodeverk(periodeType.getKodeverk());
    }

    private static MorsAktivitetsTyper morsAktivitetFra(MorsAktivitet aktivitet) {
        return Optional.ofNullable(aktivitet)
                .map(s -> morsAktivitetFra(s.name()))
                .orElse(morsAktivitetFra(UKJENT_KODEVERKSVERDI));
    }

    private static MorsAktivitetsTyper morsAktivitetFra(String aktivitet) {
        MorsAktivitetsTyper morsAktivitet = new MorsAktivitetsTyper().withKode(aktivitet);
        return morsAktivitet.withKodeverk(morsAktivitet.getKodeverk());
    }

    private static Utsettelsesaarsaker utsettelsesÅrsakFra(UtsettelsesÅrsak årsak) {
        return Optional.ofNullable(årsak)
                .map(s -> utsettelsesÅrsakFra(s.name()))
                .orElse(null);
    }

    private static Utsettelsesaarsaker utsettelsesÅrsakFra(String årsak) {
        Utsettelsesaarsaker utsettelsesÅrsak = new Utsettelsesaarsaker().withKode(årsak);
        return utsettelsesÅrsak.withKodeverk(utsettelsesÅrsak.getKodeverk());
    }

    private static Oppholdsaarsaker oppholdsÅrsakFra(Oppholdsårsak årsak) {
        return Optional.ofNullable(årsak)
                .map(s -> oppholdsÅrsakFra(s.name()))
                .orElseThrow(() -> new UnexpectedInputException("Oppholdsårsak må være satt"));
    }

    private static Oppholdsaarsaker oppholdsÅrsakFra(String årsak) {
        Oppholdsaarsaker oppholdsÅrsak = new Oppholdsaarsaker().withKode(årsak);
        return oppholdsÅrsak.withKodeverk(oppholdsÅrsak.getKodeverk());
    }

    private static Overfoeringsaarsaker påkrevdOverføringsÅrsakFra(Overføringsårsak årsak) {
        return Optional.ofNullable(årsak)
                .map(s -> overføringsÅrsakFra(s.name()))
                .orElseThrow(() -> new UnexpectedInputException("Oppholdsårsak må være satt"));
    }

    private static Overfoeringsaarsaker valgfriOverføringsÅrsakFra(Overføringsårsak årsak) {
        return Optional.ofNullable(årsak)
                .map(s -> overføringsÅrsakFra(s.name()))
                .orElse(overføringsÅrsakFra(UKJENT_KODEVERKSVERDI));
    }

    private static Overfoeringsaarsaker overføringsÅrsakFra(String årsak) {
        Overfoeringsaarsaker overføringsÅrsak = new Overfoeringsaarsaker().withKode(årsak);
        return overføringsÅrsak.withKodeverk(overføringsÅrsak.getKodeverk());
    }

    private static Rettigheter rettigheterFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.Rettigheter rettigheter, boolean ukjentForelder) {

        if (ukjentForelder) {
            return new Rettigheter()
                    .withHarOmsorgForBarnetIPeriodene(true)
                    .withHarAnnenForelderRett(false)
                    .withHarAleneomsorgForBarnet(true);
        }
        if (rettigheter == null) {
            return null;
        }
        return new Rettigheter()
                .withHarOmsorgForBarnetIPeriodene(true) // Hardkodet til true, siden dette er implisitt og vi ikke spør
                                                        // brukeren eksplisitt
                .withHarAnnenForelderRett(rettigheter.isHarAnnenForelderRett())
                .withHarAleneomsorgForBarnet(rettigheter.isHarAleneOmsorgForBarnet());
    }

    private AnnenForelder annenForelderFra(
            no.nav.foreldrepenger.mottak.domain.felles.annenforelder.AnnenForelder annenForelder) {

        if (erAnnenForelderUkjent(annenForelder)) {
            return ukjentForelder();
        }
        if (annenForelder instanceof UtenlandskForelder) {
            return utenlandskForelder(UtenlandskForelder.class.cast(annenForelder));
        }
        if (annenForelder instanceof NorskForelder) {
            return norskForelder(NorskForelder.class.cast(annenForelder));
        }
        return null;
    }

    private static UkjentForelder ukjentForelder() {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v3.UkjentForelder();
    }

    private static AnnenForelderUtenNorskIdent utenlandskForelder(UtenlandskForelder utenlandskForelder) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderUtenNorskIdent()
                .withUtenlandskPersonidentifikator(utenlandskForelder.getId())
                .withLand(landFra(utenlandskForelder.getLand()));
    }

    private AnnenForelderMedNorskIdent norskForelder(NorskForelder norskForelder) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderMedNorskIdent()
                .withAktoerId(oppslag.getAktørId(norskForelder.getFnr()).getId());
    }

    private static SoekersRelasjonTilBarnet relasjonFra(RelasjonTilBarn relasjonTilBarn) {

        if (relasjonTilBarn == null) {
            return null;
        }

        if (relasjonTilBarn instanceof Fødsel) {
            Fødsel fødsel = Fødsel.class.cast(relasjonTilBarn);
            return new Foedsel()
                    .withVedlegg(relasjonTilBarnVedleggFra(relasjonTilBarn.getVedlegg()))
                    .withFoedselsdato(fødsel.getFødselsdato().get(0))
                    .withAntallBarn(fødsel.getAntallBarn());
        }
        if (relasjonTilBarn instanceof FremtidigFødsel) {
            FremtidigFødsel termin = FremtidigFødsel.class.cast(relasjonTilBarn);
            return new Termin()
                    .withVedlegg(relasjonTilBarnVedleggFra(relasjonTilBarn.getVedlegg()))
                    .withAntallBarn(termin.getAntallBarn())
                    .withTermindato(termin.getTerminDato())
                    .withUtstedtdato(termin.getUtstedtDato());
        }
        if (relasjonTilBarn instanceof Adopsjon) {
            Adopsjon adopsjon = Adopsjon.class.cast(relasjonTilBarn);
            return new no.nav.vedtak.felles.xml.soeknad.felles.v3.Adopsjon()
                    .withVedlegg(relasjonTilBarnVedleggFra(relasjonTilBarn.getVedlegg()))
                    .withAntallBarn(adopsjon.getAntallBarn())
                    .withFoedselsdato(adopsjon.getFødselsdato())
                    .withOmsorgsovertakelsesdato(adopsjon.getOmsorgsovertakelsesdato())
                    .withAdopsjonAvEktefellesBarn(adopsjon.isEktefellesBarn())
                    .withAnkomstdato(adopsjon.getAnkomstDato());
        }
        if (relasjonTilBarn instanceof Omsorgsovertakelse) {
            Omsorgsovertakelse omsorgsovertakelse = Omsorgsovertakelse.class.cast(relasjonTilBarn);
            return new no.nav.vedtak.felles.xml.soeknad.felles.v3.Omsorgsovertakelse()
                    .withVedlegg(relasjonTilBarnVedleggFra(relasjonTilBarn.getVedlegg()))
                    .withAntallBarn(omsorgsovertakelse.getAntallBarn())
                    .withFoedselsdato(omsorgsovertakelse.getFødselsdato())
                    .withOmsorgsovertakelsesdato(omsorgsovertakelse.getOmsorgsovertakelsesdato())
                    .withOmsorgsovertakelseaarsak(new Omsorgsovertakelseaarsaker().withKode("OVERTATT_OMSORG"))
                    .withBeskrivelse("Omsorgsovertakelse");
        }

        throw new UnexpectedInputException(
                "Relasjon " + relasjonTilBarn.getClass().getSimpleName() + " er ikke støttet");
    }

    private static List<JAXBElement<Object>> relasjonTilBarnVedleggFra(List<String> vedlegg) {
        return vedlegg.stream()
                .map(s -> FELLES_FACTORY_V3.createSoekersRelasjonTilBarnetVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static Bruker søkerFra(AktorId aktørId, Søker søker) {
        return new Bruker()
                .withAktoerId(aktørId.getId())
                .withSoeknadsrolle(brukerRolleFra(søker.getSøknadsRolle()));
    }

    private static Brukerroller brukerRolleFra(BrukerRolle søknadsRolle) {
        return brukerRolleFra(søknadsRolle.name());
    }

    private static Brukerroller brukerRolleFra(String rolle) {
        Brukerroller brukerRolle = new Brukerroller().withKode(rolle);
        return brukerRolle.withKodeverk(brukerRolle.getKodeverk());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", mapperEgenskaper=" + mapperEgenskaper() + "]";
    }

}
