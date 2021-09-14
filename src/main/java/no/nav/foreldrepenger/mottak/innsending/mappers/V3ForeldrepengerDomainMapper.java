package no.nav.foreldrepenger.mottak.innsending.mappers;

import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.landFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.medlemsskapFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.målformFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.opptjeningFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.søkerFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.vedleggFra;
import static no.nav.foreldrepenger.mottak.util.Constants.UKJENT_KODEVERKSVERDI;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V3;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Omsorgsovertakelse;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.FriUtsettelsesPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.GradertUttaksPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.OppholdsPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Oppholdsårsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.OverføringsPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UttaksPeriode;
import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.jaxb.FPV3JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v3.Endringssoeknad;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderMedNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderUtenNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Foedsel;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Rettigheter;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.SoekersRelasjonTilBarnet;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Termin;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.UkjentForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Dekningsgrad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Foreldrepenger;
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
    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V3, ENDRING_FORELDREPENGER,
            INITIELL_FORELDREPENGER);
    private static final FPV3JAXBUtil JAXB = new FPV3JAXBUtil();
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
    public String tilXML(Søknad søknad, AktørId søker, SøknadEgenskap egenskap) {
        return JAXB.marshal(SØKNAD_FACTORY_V3.createSoeknad(tilModell(søknad, søker)));
    }

    @Override
    public String tilXML(Endringssøknad endringssøknad, AktørId søker, SøknadEgenskap egenskap) {
        return JAXB.marshal(SØKNAD_FACTORY_V3.createSoeknad(tilModell(endringssøknad, søker)));
    }

    private Soeknad tilModell(Søknad søknad, AktørId søker) {
        return new Soeknad()
                .withSprakvalg(målformFra(søknad.getSøker()))
                .withAndreVedlegg(vedleggFra(søknad.getFrivilligeVedlegg()))
                .withPaakrevdeVedlegg(vedleggFra(søknad.getPåkrevdeVedlegg()))
                .withSoeker(søkerFra(søker, søknad.getSøker()))
                .withOmYtelse(ytelseFra(søknad))
                .withMottattDato(søknad.getMottattdato())
                .withBegrunnelseForSenSoeknad(søknad.getBegrunnelseForSenSøknad())
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger());
    }

    private static Soeknad tilModell(Endringssøknad endringsøknad, AktørId søker) {
        return new Soeknad()
                .withSprakvalg(målformFra(endringsøknad.getSøker()))
                .withMottattDato(endringsøknad.getMottattdato())
                .withSoeker(søkerFra(søker, endringsøknad.getSøker()))
                .withAndreVedlegg(vedleggFra(endringsøknad.getFrivilligeVedlegg()))
                .withPaakrevdeVedlegg(vedleggFra(endringsøknad.getPåkrevdeVedlegg()))
                .withOmYtelse(ytelseFra(endringsøknad));
    }

    private static JAXBElement<Endringssoeknad> endringssøknadFra(Endringssøknad endringssøknad) {
        return ENDRING_FACTORY_V3.createEndringssoeknad(new Endringssoeknad()
                .withFordeling(fordelingFra(endringssøknad))
                .withSaksnummer(endringssøknad.getSaksnr()));
    }

    private JAXBElement<Foreldrepenger> foreldrepengerFra(
            no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger ytelse) {
        return FP_FACTORY_V3.createForeldrepenger(new Foreldrepenger()
                .withDekningsgrad(dekningsgradFra(ytelse.getDekningsgrad()))
                .withMedlemskap(medlemsskapFra(ytelse.getMedlemsskap(), ytelse.getRelasjonTilBarn().relasjonsDato()))
                .withOpptjening(opptjeningFra(ytelse.getOpptjening()))
                .withFordeling(fordelingFra(ytelse.getFordeling()))
                .withRettigheter(
                        rettigheterFra(ytelse.getRettigheter(), erAnnenForelderUkjent(ytelse.getAnnenForelder())))
                .withAnnenForelder(annenForelderFra(ytelse.getAnnenForelder()))
                .withRelasjonTilBarnet(relasjonFra(ytelse.getRelasjonTilBarn())));
    }

    private static OmYtelse ytelseFra(Endringssøknad endringssøknad) {
        return new OmYtelse().withAny(endringssøknadFra(endringssøknad));
    }

    private OmYtelse ytelseFra(Søknad søknad) {
        return new OmYtelse()
                .withAny(JAXB.marshalToElement(
                        foreldrepengerFra(no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger.class
                                .cast(søknad.getYtelse()))));
    }

    private static Fordeling fordelingFra(Endringssøknad endringssøknad) {
        return fordelingFra(no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger.class
                .cast(endringssøknad.getYtelse()).getFordeling());
    }

    private static boolean erAnnenForelderUkjent(
            no.nav.foreldrepenger.common.domain.felles.annenforelder.AnnenForelder annenForelder) {
        return annenForelder instanceof no.nav.foreldrepenger.common.domain.felles.annenforelder.UkjentForelder;
    }

    private static Dekningsgrad dekningsgradFra(
            no.nav.foreldrepenger.common.domain.foreldrepenger.Dekningsgrad dekningsgrad) {
        return Optional.ofNullable(dekningsgrad)
                .map(no.nav.foreldrepenger.common.domain.foreldrepenger.Dekningsgrad::kode)
                .map(V3ForeldrepengerDomainMapper::dekningsgradFra)
                .map(g -> new Dekningsgrad().withDekningsgrad(g))
                .orElse(null);
    }

    private static Dekningsgrader dekningsgradFra(Integer kode) {
        var dekningsgrad = new Dekningsgrader().withKode(String.valueOf(kode));
        return dekningsgrad.withKodeverk(dekningsgrad.getKodeverk());
    }

    private static Fordeling fordelingFra(
            no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Fordeling fordeling) {
        return Optional.ofNullable(fordeling)
                .map(V3ForeldrepengerDomainMapper::create)
                .orElse(null);
    }

    private static Fordeling create(no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Fordeling fordeling) {
        return new Fordeling()
                .withPerioder(perioderFra(fordeling.getPerioder()))
                .withOenskerKvoteOverfoert(valgfriOverføringsÅrsakFra(fordeling.getØnskerKvoteOverført()))
                .withAnnenForelderErInformert(fordeling.isErAnnenForelderInformert());
    }

    private static List<no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg> perioderFra(
            List<LukketPeriodeMedVedlegg> perioder) {
        return safeStream(perioder)
                .map(V3ForeldrepengerDomainMapper::lukketPeriodeFra)
                .toList();
    }

    private static List<JAXBElement<Object>> lukketPeriodeVedleggFra(List<String> vedlegg) {
        return safeStream(vedlegg)
                .map(s -> UTTAK_FACTORY_V3.createLukketPeriodeMedVedleggVedlegg(new Vedlegg().withId(s)))
                .toList();
    }

    private static no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg lukketPeriodeFra(LukketPeriodeMedVedlegg p) {
        return Optional.ofNullable(p)
                .map(V3ForeldrepengerDomainMapper::create)
                .orElse(null);
    }

    private static no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg create(
            LukketPeriodeMedVedlegg periode) {
        if (periode instanceof OverføringsPeriode p) {
            return create(p);
        }
        if (periode instanceof OppholdsPeriode p) {
            return create(p);
        }
        if (periode instanceof FriUtsettelsesPeriode p) {
            return create(p);
        }
        if (periode instanceof UtsettelsesPeriode p) {
            return create(p);
        }
        if (periode instanceof GradertUttaksPeriode p) {
            return create(p);
        }

        if (periode instanceof UttaksPeriode p) {
            return create(p);
        }
        throw new UnexpectedInputException("Ukjent periode " + periode.getClass().getSimpleName());
    }

    private static no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg create(OverføringsPeriode o) {
        return new Overfoeringsperiode()
                .withFom(o.getFom())
                .withTom(o.getTom())
                .withOverfoeringAv(uttaksperiodeTypeFra(o.getUttaksperiodeType()))
                .withAarsak(påkrevdOverføringsÅrsakFra(o.getÅrsak()))
                .withVedlegg(lukketPeriodeVedleggFra(o.getVedlegg()));
    }

    private static no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg create(OppholdsPeriode o) {
        return new Oppholdsperiode()
                .withFom(o.getFom())
                .withTom(o.getTom())
                .withAarsak(oppholdsÅrsakFra(o.getÅrsak()))
                .withVedlegg(lukketPeriodeVedleggFra(o.getVedlegg()));
    }

    private static no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg create(UtsettelsesPeriode u) {
        return new Utsettelsesperiode()
                .withFom(u.getFom())
                .withTom(u.getTom())
                .withErArbeidstaker(u.isErArbeidstaker())
                .withMorsAktivitetIPerioden(morsAktivitetFra(u.getMorsAktivitetsType()))
                .withUtsettelseAv(uttaksperiodeTypeFra(u.getUttaksperiodeType(), true))
                .withAarsak(utsettelsesÅrsakFra(u.getÅrsak()))
                .withVedlegg(lukketPeriodeVedleggFra(u.getVedlegg()));
    }

    private static no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg create(FriUtsettelsesPeriode p) {
        return new Utsettelsesperiode()
                .withFom(p.getFom())
                .withTom(p.getTom())
                .withUtsettelseAv(uttaksperiodeTypeFra(p.getUttaksperiodeType(), true))
                .withMorsAktivitetIPerioden(morsAktivitetFra(p.getMorsAktivitetsType(), true))
                .withAarsak(utsettelsesÅrsakFra(p.getÅrsak()))
                .withVedlegg(lukketPeriodeVedleggFra(p.getVedlegg()));
    }

    private static no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg create(GradertUttaksPeriode g) {
        var gradering = new Gradering()
                .withFom(g.getFom())
                .withTom(g.getTom())
                .withType(uttaksperiodeTypeFra(g.getUttaksperiodeType()))
                .withOenskerSamtidigUttak(g.isØnskerSamtidigUttak())
                .withMorsAktivitetIPerioden(morsAktivitetFra(g.getMorsAktivitetsType()))
                .withOenskerFlerbarnsdager(g.isØnskerFlerbarnsdager())
                .withErArbeidstaker(g.isErArbeidstaker())
                .withArbeidtidProsent(prosentFra(g.getArbeidstidProsent()))
                .withArbeidsgiver(arbeidsgiverFra(g.getVirksomhetsnummer()))
                .withArbeidsforholdSomSkalGraderes(g.isArbeidsForholdSomskalGraderes())
                .withVedlegg(lukketPeriodeVedleggFra(g.getVedlegg()));
        Optional.ofNullable(g.getFrilans()).ifPresent(p -> gradering.setErFrilanser(p.booleanValue()));
        Optional.ofNullable(g.getSelvstendig()).ifPresent(p -> gradering.setErSelvstNæringsdrivende(p.booleanValue()));
        return g.isØnskerSamtidigUttak()
                ? gradering.withSamtidigUttakProsent(prosentFra(g.getSamtidigUttakProsent()))
                : gradering;
    }

    private static no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg create(UttaksPeriode u) {
        return new Uttaksperiode()
                .withFom(u.getFom())
                .withTom(u.getTom())
                .withSamtidigUttakProsent(prosentFra(u.getSamtidigUttakProsent()))
                .withOenskerFlerbarnsdager(u.isØnskerFlerbarnsdager())
                .withType(uttaksperiodeTypeFra(u.getUttaksperiodeType()))
                .withOenskerSamtidigUttak(u.isØnskerSamtidigUttak())
                .withMorsAktivitetIPerioden(morsAktivitetFra(u.getMorsAktivitetsType()))
                .withVedlegg(lukketPeriodeVedleggFra(u.getVedlegg()));
    }

    private static double prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::getProsent)
                .orElse(0d);
    }

    private static Arbeidsgiver arbeidsgiverFra(List<String> arbeidsgiver) {
        if (isEmpty(arbeidsgiver)) {
            return null;
        }
        return Optional.ofNullable(arbeidsgiver.get(0))
                .map(V3ForeldrepengerDomainMapper::arbeidsgiverFra)
                .orElse(null);
    }

    private static Arbeidsgiver arbeidsgiverFra(String id) {
        return switch (id.length()) {
            case 11 -> new Person().withIdentifikator(id);
            case 9 -> new Virksomhet().withIdentifikator(id);
            default -> throw new UnexpectedInputException("Ugyldig lengde " + id.length() + " for arbeidsgiver");
        };
    }

    private static Uttaksperiodetyper uttaksperiodeTypeFra(StønadskontoType type) {
        return uttaksperiodeTypeFra(type, false);
    }

    private static Uttaksperiodetyper uttaksperiodeTypeFra(StønadskontoType type, boolean optional) {
        if (optional) {
            return Optional.ofNullable(type)
                    .map(StønadskontoType::name)
                    .map(V3ForeldrepengerDomainMapper::uttaksperiodeTypeFra)
                    .orElse(null);
        }
        return Optional.ofNullable(type)
                .map(StønadskontoType::name)
                .map(V3ForeldrepengerDomainMapper::uttaksperiodeTypeFra)
                .orElseThrow(() -> new UnexpectedInputException("Stønadskontotype må være satt"));
    }

    private static Uttaksperiodetyper uttaksperiodeTypeFra(String type) {
        var periodeType = new Uttaksperiodetyper().withKode(type);
        return periodeType.withKodeverk(periodeType.getKodeverk());
    }

    private static MorsAktivitetsTyper morsAktivitetFra(MorsAktivitet aktivitet) {
        return morsAktivitetFra(aktivitet, false);
    }

    private static MorsAktivitetsTyper morsAktivitetFra(MorsAktivitet aktivitet, boolean optional) {
        if (optional) {
            return Optional.ofNullable(aktivitet)
                    .map(MorsAktivitet::name)
                    .map(V3ForeldrepengerDomainMapper::morsAktivitetFra)
                    .orElse(null);
        }
        return Optional.ofNullable(aktivitet)
                .map(MorsAktivitet::name)
                .map(V3ForeldrepengerDomainMapper::morsAktivitetFra)
                .orElse(morsAktivitetFra(UKJENT_KODEVERKSVERDI));
    }

    private static MorsAktivitetsTyper morsAktivitetFra(String aktivitet) {
        var morsAktivitet = new MorsAktivitetsTyper().withKode(aktivitet);
        return morsAktivitet.withKodeverk(morsAktivitet.getKodeverk());
    }

    private static Utsettelsesaarsaker utsettelsesÅrsakFra(UtsettelsesÅrsak årsak) {
        return Optional.ofNullable(årsak)
                .map(UtsettelsesÅrsak::name)
                .map(V3ForeldrepengerDomainMapper::utsettelsesÅrsakFra)
                .orElse(null);
    }

    private static Utsettelsesaarsaker utsettelsesÅrsakFra(String årsak) {
        var utsettelsesÅrsak = new Utsettelsesaarsaker().withKode(årsak);
        return utsettelsesÅrsak.withKodeverk(utsettelsesÅrsak.getKodeverk());
    }

    private static Oppholdsaarsaker oppholdsÅrsakFra(Oppholdsårsak årsak) {
        return Optional.ofNullable(årsak)
                .map(Oppholdsårsak::name)
                .map(V3ForeldrepengerDomainMapper::oppholdsÅrsakFra)
                .orElseThrow(() -> new UnexpectedInputException("Oppholdsårsak må være satt"));
    }

    private static Oppholdsaarsaker oppholdsÅrsakFra(String årsak) {
        Oppholdsaarsaker oppholdsÅrsak = new Oppholdsaarsaker().withKode(årsak);
        return oppholdsÅrsak.withKodeverk(oppholdsÅrsak.getKodeverk());
    }

    private static Overfoeringsaarsaker påkrevdOverføringsÅrsakFra(Overføringsårsak årsak) {
        return Optional.ofNullable(årsak)
                .map(Overføringsårsak::name)
                .map(V3ForeldrepengerDomainMapper::overføringsÅrsakFra)
                .orElseThrow(() -> new UnexpectedInputException("Oppholdsårsak må være satt"));
    }

    private static Overfoeringsaarsaker valgfriOverføringsÅrsakFra(Overføringsårsak årsak) {
        return Optional.ofNullable(årsak)
                .map(Overføringsårsak::name)
                .map(V3ForeldrepengerDomainMapper::overføringsÅrsakFra)
                .orElse(overføringsÅrsakFra(UKJENT_KODEVERKSVERDI));
    }

    private static Overfoeringsaarsaker overføringsÅrsakFra(String årsak) {
        var overføringsÅrsak = new Overfoeringsaarsaker().withKode(årsak);
        return overføringsÅrsak.withKodeverk(overføringsÅrsak.getKodeverk());
    }

    private static Rettigheter rettigheterFra(no.nav.foreldrepenger.common.domain.foreldrepenger.Rettigheter r, boolean ukjentForelder) {
        if (ukjentForelder) {
            return rettigheterForUkjentForelder();
        }
        return Optional.ofNullable(r)
                .map(V3ForeldrepengerDomainMapper::create)
                .orElse(null);
    }

    private static Rettigheter rettigheterForUkjentForelder() {
        return new Rettigheter()
                .withHarOmsorgForBarnetIPeriodene(true)
                .withHarAnnenForelderRett(false)
                .withHarAleneomsorgForBarnet(true);
    }

    private static Rettigheter create(no.nav.foreldrepenger.common.domain.foreldrepenger.Rettigheter r) {
        return new Rettigheter()
                .withHarOmsorgForBarnetIPeriodene(true)
                .withHarAnnenForelderRett(r.isHarAnnenForelderRett())
                .withHarAleneomsorgForBarnet(r.isHarAleneOmsorgForBarnet());
    }

    private AnnenForelder annenForelderFra(
            no.nav.foreldrepenger.common.domain.felles.annenforelder.AnnenForelder annenForelder) {
        if (erAnnenForelderUkjent(annenForelder)) {
            return new UkjentForelder();
        }
        if (annenForelder instanceof UtenlandskForelder u) {
            return utenlandskForelder(u);
        }
        if (annenForelder instanceof NorskForelder n) {
            return norskForelder(n);
        }
        return null;
    }

    private static AnnenForelderUtenNorskIdent utenlandskForelder(UtenlandskForelder utenlandskForelder) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderUtenNorskIdent()
                .withUtenlandskPersonidentifikator(utenlandskForelder.getId())
                .withLand(landFra(utenlandskForelder.getLand()));
    }

    private AnnenForelderMedNorskIdent norskForelder(NorskForelder norskForelder) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderMedNorskIdent()
                .withAktoerId(oppslag.aktørId(norskForelder.getFnr()).getId());
    }

    private static SoekersRelasjonTilBarnet relasjonFra(RelasjonTilBarn relasjonTilBarn) {
        return Optional.ofNullable(relasjonTilBarn)
                .map(V3ForeldrepengerDomainMapper::create)
                .orElse(null);
    }

    private static SoekersRelasjonTilBarnet create(RelasjonTilBarn relasjonTilBarn) {
        if (relasjonTilBarn instanceof Fødsel f) {
            return create(f);
        }
        if (relasjonTilBarn instanceof FremtidigFødsel f) {
            return create(f);
        }
        if (relasjonTilBarn instanceof Adopsjon a) {
            return create(a);
        }
        if (relasjonTilBarn instanceof Omsorgsovertakelse o) {
            return create(o);
        }
        throw new UnexpectedInputException(
                "Relasjon " + relasjonTilBarn.getClass().getSimpleName() + " er ikke støttet");
    }

    private static SoekersRelasjonTilBarnet create(Omsorgsovertakelse omsorgsovertakelse) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v3.Omsorgsovertakelse()
                .withVedlegg(relasjonTilBarnVedleggFra(omsorgsovertakelse.getVedlegg()))
                .withAntallBarn(omsorgsovertakelse.getAntallBarn())
                .withFoedselsdato(omsorgsovertakelse.getFødselsdato())
                .withOmsorgsovertakelsesdato(omsorgsovertakelse.getOmsorgsovertakelsesdato())
                .withOmsorgsovertakelseaarsak(new Omsorgsovertakelseaarsaker().withKode("OVERTATT_OMSORG"))
                .withBeskrivelse("Omsorgsovertakelse");
    }

    private static SoekersRelasjonTilBarnet create(Fødsel fødsel) {
        return new Foedsel()
                .withVedlegg(relasjonTilBarnVedleggFra(fødsel.getVedlegg()))
                .withFoedselsdato(fødsel.getFødselsdato().get(0))
                .withTermindato(fødsel.getTermindato())
                .withAntallBarn(fødsel.getAntallBarn());
    }

    private static SoekersRelasjonTilBarnet create(FremtidigFødsel termin) {
        return new Termin()
                .withVedlegg(relasjonTilBarnVedleggFra(termin.getVedlegg()))
                .withAntallBarn(termin.getAntallBarn())
                .withTermindato(termin.getTerminDato())
                .withUtstedtdato(termin.getUtstedtDato());
    }

    private static SoekersRelasjonTilBarnet create(Adopsjon adopsjon) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v3.Adopsjon()
                .withVedlegg(relasjonTilBarnVedleggFra(adopsjon.getVedlegg()))
                .withAntallBarn(adopsjon.getAntallBarn())
                .withFoedselsdato(adopsjon.getFødselsdato())
                .withOmsorgsovertakelsesdato(adopsjon.getOmsorgsovertakelsesdato())
                .withAdopsjonAvEktefellesBarn(adopsjon.isEktefellesBarn())
                .withAnkomstdato(adopsjon.getAnkomstDato());
    }

    private static List<JAXBElement<Object>> relasjonTilBarnVedleggFra(List<String> vedlegg) {
        return safeStream(vedlegg)
                .map(s -> FELLES_FACTORY_V3.createSoekersRelasjonTilBarnetVedlegg(new Vedlegg().withId(s)))
                .toList();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", mapperEgenskaper=" + mapperEgenskaper() + "]";
    }
}
