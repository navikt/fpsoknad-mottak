package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.mappers.V3XMLMapperCommon.tilLand;
import static no.nav.foreldrepenger.mottak.innsyn.mappers.V3XMLMapperCommon.tilOpptjening;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V3;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.PåkrevdVedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.VedleggMetaData;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Fødsel;
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
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.jaxb.FPV3JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v3.Endringssoeknad;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderMedNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderUtenNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Foedsel;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Rettigheter;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.SoekersRelasjonTilBarnet;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Termin;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.UkjentForelder;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Dekningsgrad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Innsendingstype;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.MorsAktivitetsTyper;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Oppholdsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Overfoeringsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Utsettelsesaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Uttaksperiodetyper;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Arbeidsgiver;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Gradering;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Oppholdsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Overfoeringsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Utsettelsesperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Uttaksperiode;
import no.nav.vedtak.felles.xml.soeknad.v3.OmYtelse;
import no.nav.vedtak.felles.xml.soeknad.v3.Soeknad;

@Component
public class V3ForeldrepengerXMLMapper extends AbstractXMLMapper {
    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V3, ENDRING_FORELDREPENGER,
            INITIELL_FORELDREPENGER);

    private static final Logger LOG = LoggerFactory.getLogger(V3ForeldrepengerXMLMapper.class);

    private static final FPV3JAXBUtil JAXB = new FPV3JAXBUtil();

    public V3ForeldrepengerXMLMapper(Oppslag oppslag) {
        super(oppslag);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return EGENSKAPER;
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        if (xml == null) {
            LOG.debug("Ingen søknad ble funnet");
            return null;
        }
        try {
            Soeknad søknad = JAXB.unmarshalToElement(xml, Soeknad.class).getValue();
            switch (egenskap.getType()) {
            case ENDRING_FORELDREPENGER:
                Endringssøknad endringssøknad = new Endringssøknad(
                        søknad.getMottattDato().atStartOfDay(),
                        tilSøker(søknad.getSoeker()),
                        tilYtelse(søknad.getOmYtelse()).getFordeling(), saksnummer(søknad.getOmYtelse()));
                endringssøknad.setTilleggsopplysninger(søknad.getTilleggsopplysninger());
                endringssøknad.setBegrunnelseForSenSøknad(søknad.getBegrunnelseForSenSoeknad());
                return endringssøknad;
            case INITIELL_FORELDREPENGER:
                Søknad førstegangssøknad = new Søknad(
                        søknad.getMottattDato().atStartOfDay(),
                        tilSøker(søknad.getSoeker()),
                        tilYtelse(søknad.getOmYtelse()),
                        tilVedlegg(søknad.getPaakrevdeVedlegg(), søknad.getAndreVedlegg()));
                førstegangssøknad.setTilleggsopplysninger(søknad.getTilleggsopplysninger());
                førstegangssøknad.setBegrunnelseForSenSøknad(søknad.getBegrunnelseForSenSoeknad());
                return førstegangssøknad;
            default:
                LOG.warn("Ukjent søknad");
                return null;
            }
        } catch (Exception e) {
            LOG.debug("Feil ved unmarshalling av søknad, ikke kritisk foreløpig, vi bruker ikke dette til noe", e);
            return null;
        }
    }

    private List<Vedlegg> tilVedlegg(List<no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg> påkrevd,
            List<no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg> valgfritt) {
        Stream<Vedlegg> vf = safeStream(valgfritt)
                .map(this::metadataFra)
                .map(s -> new ValgfrittVedlegg(s, null));
        Stream<Vedlegg> pk = safeStream(påkrevd)
                .map(this::metadataFra)
                .map(s -> new PåkrevdVedlegg(s, null));
        return Stream.concat(vf, pk).collect(toList());
    }

    private VedleggMetaData metadataFra(no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg vedlegg) {
        return new VedleggMetaData(
                vedlegg.getId(),
                tilInnsendingsType(vedlegg.getInnsendingstype()),
                tilDokumentType(vedlegg.getSkjemanummer()));
    }

    private static DokumentType tilDokumentType(String skjemanummer) {
        return DokumentType.valueOf(skjemanummer);
    }

    private static InnsendingsType tilInnsendingsType(Innsendingstype innsendingstype) {
        return InnsendingsType.valueOf(innsendingstype.getKode());
    }

    private static String saksnummer(OmYtelse omYtelse) {
        Object ytelse = ytelse(omYtelse);
        if (ytelse instanceof Endringssoeknad) {
            return Endringssoeknad.class.cast(ytelse).getSaksnummer();
        }
        throw new UnexpectedInputException(ytelse.getClass().getSimpleName() + " er ikke en endringssøknad");
    }

    private static Object ytelse(OmYtelse omYtelse) {
        if (omYtelse == null || omYtelse.getAny() == null || omYtelse.getAny().isEmpty()) {
            LOG.warn("Ingen ytelse i søknaden");
            return null;
        }
        if (omYtelse.getAny().size() > 1) {
            LOG.warn("Fikk {} ytelser i søknaden, forventet 1, behandler kun den første", omYtelse.getAny().size());
        }
        return ((JAXBElement<?>) omYtelse.getAny().get(0)).getValue();
    }

    private no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger tilYtelse(OmYtelse omYtelse) {

        Object førsteYtelse = ytelse(omYtelse);
        if (førsteYtelse instanceof Endringssoeknad) {
            Endringssoeknad søknad = Endringssoeknad.class.cast(førsteYtelse);
            return no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.builder()
                    .fordeling(tilFordeling(søknad.getFordeling()))
                    .build();
        }

        if (førsteYtelse instanceof Foreldrepenger) {
            Foreldrepenger søknad = Foreldrepenger.class.cast(førsteYtelse);
            return no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.builder()
                    .annenForelder(tilAnnenForelder(søknad.getAnnenForelder()))
                    .dekningsgrad(tilDekningsgrad(søknad.getDekningsgrad()))
                    .fordeling(tilFordeling(søknad.getFordeling()))
                    .medlemsskap(tilMedlemsskap(søknad.getMedlemskap()))
                    .opptjening(tilOpptjening(søknad.getOpptjening()))
                    .relasjonTilBarn(tilRelasjonTilBarn(søknad.getRelasjonTilBarnet()))
                    .rettigheter(tilRettigheter(søknad.getRettigheter()))
                    .build();
        }
        throw new UnexpectedInputException("Ukjent type " + førsteYtelse.getClass().getSimpleName());
    }

    private static no.nav.foreldrepenger.mottak.domain.foreldrepenger.Rettigheter tilRettigheter(
            Rettigheter rettigheter) {
        if (rettigheter == null) {
            return null;
        }
        return new no.nav.foreldrepenger.mottak.domain.foreldrepenger.Rettigheter(
                rettigheter.isHarAnnenForelderRett(),
                rettigheter.isHarOmsorgForBarnetIPeriodene(),
                rettigheter.isHarAleneomsorgForBarnet(),
                null);
    }

    private static RelasjonTilBarn tilRelasjonTilBarn(SoekersRelasjonTilBarnet relasjonTilBarnet) {
        if (relasjonTilBarnet == null) {
            return null;
        }
        if (relasjonTilBarnet instanceof Foedsel) {
            Foedsel fødsel = Foedsel.class.cast(relasjonTilBarnet);
            return new Fødsel(
                    fødsel.getAntallBarn(),
                    fødsel.getFoedselsdato(),
                    fødsel.getTermindato());
        }
        if (relasjonTilBarnet instanceof Termin) {
            Termin termin = Termin.class.cast(relasjonTilBarnet);
            return new FremtidigFødsel(
                    termin.getAntallBarn(),
                    termin.getTermindato(),
                    termin.getUtstedtdato(),
                    emptyList());
        }
        if (relasjonTilBarnet instanceof no.nav.vedtak.felles.xml.soeknad.felles.v3.Adopsjon) {
            no.nav.vedtak.felles.xml.soeknad.felles.v3.Adopsjon adopsjon = no.nav.vedtak.felles.xml.soeknad.felles.v3.Adopsjon.class
                    .cast(relasjonTilBarnet);
            return new Adopsjon(
                    adopsjon.getAntallBarn(),
                    adopsjon.getOmsorgsovertakelsesdato(),
                    adopsjon.isAdopsjonAvEktefellesBarn(),
                    emptyList(),
                    adopsjon.getAnkomstdato(),
                    adopsjon.getFoedselsdato());
        }
        throw new UnexpectedInputException("Ikke-støttet type " + relasjonTilBarnet.getClass().getSimpleName());
    }

    private static Medlemsskap tilMedlemsskap(Medlemskap medlemskap) {
        return new Medlemsskap(
                tilTidligereOpphold(medlemskap),
                tilFremtidigOpphold(medlemskap));
    }

    private static TidligereOppholdsInformasjon tilTidligereOpphold(Medlemskap medlemskap) {
        return new TidligereOppholdsInformasjon(
                ArbeidsInformasjon.ARBEIDET_I_NORGE,
                emptyList()); // TODO
    }

    private static FramtidigOppholdsInformasjon tilFremtidigOpphold(Medlemskap medlemskap) {
        return new FramtidigOppholdsInformasjon(
                emptyList()); // TODO
    }

    private static no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Fordeling tilFordeling(
            Fordeling fordeling) {
        if (fordeling == null) {
            return null;
        }
        return new no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Fordeling(
                fordeling.isAnnenForelderErInformert(),
                tilÅrsak(fordeling.getOenskerKvoteOverfoert()),
                tilPerioder(fordeling.getPerioder()));
    }

    private static Overføringsårsak tilÅrsak(Overfoeringsaarsaker årsak) {
        if (årsak == null || årsak.getKode().equals(UKJENT_KODEVERKSVERDI)) {
            return null;
        }
        return Overføringsårsak.valueOf(årsak.getKode());
    }

    private static List<LukketPeriodeMedVedlegg> tilPerioder(
            List<no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg> perioder) {
        return safeStream(perioder)
                .map(V3ForeldrepengerXMLMapper::tilLukketPeriode)
                .collect(toList());
    }

    private static LukketPeriodeMedVedlegg tilLukketPeriode(
            no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg periode) {

        if (periode == null) {
            return null;
        }
        if (periode instanceof Overfoeringsperiode) {
            Overfoeringsperiode overføringsPeriode = Overfoeringsperiode.class.cast(periode);
            return new OverføringsPeriode(
                    overføringsPeriode.getFom(),
                    overføringsPeriode.getTom(),
                    tilÅrsak(overføringsPeriode.getAarsak()),
                    tilStønadKontoType(overføringsPeriode.getOverfoeringAv()),
                    emptyList());
        }
        if (periode instanceof Oppholdsperiode) {
            Oppholdsperiode oppholdsPeriode = Oppholdsperiode.class.cast(periode);
            return new OppholdsPeriode(
                    oppholdsPeriode.getFom(),
                    oppholdsPeriode.getTom(),
                    tilÅrsak(oppholdsPeriode.getAarsak()),
                    emptyList());
        }
        if (periode instanceof Utsettelsesperiode) {
            Utsettelsesperiode utsettelse = Utsettelsesperiode.class.cast(periode);
            return new UtsettelsesPeriode(
                    utsettelse.getFom(),
                    utsettelse.getTom(),
                    utsettelse.isErArbeidstaker(),
                    null,
                    tilÅrsak(utsettelse.getAarsak()),
                    tilStønadKontoType(utsettelse.getUtsettelseAv()),
                    tilMorsAktivitet(utsettelse.getMorsAktivitetIPerioden()),
                    emptyList());
        }

        if (periode instanceof Gradering) {
            Gradering gradering = Gradering.class.cast(periode);
            return new GradertUttaksPeriode(
                    gradering.getFom(),
                    gradering.getTom(),
                    tilStønadKontoType(gradering.getType()),
                    gradering.isOenskerSamtidigUttak(),
                    tilMorsAktivitet(gradering.getMorsAktivitetIPerioden()),
                    gradering.isOenskerFlerbarnsdager(),
                    gradering.getSamtidigUttakProsent(),
                    gradering.getArbeidtidProsent(),
                    gradering.isErArbeidstaker(),
                    gradering.isArbeidsforholdSomSkalGraderes(),
                    tilArbeidsgiver(gradering.getArbeidsgiver()),
                    tilBoolean(gradering.isErFrilanser()),
                    tilBoolean(gradering.isErSelvstNæringsdrivende()),
                    emptyList());
        }

        if (periode instanceof Uttaksperiode) {
            Uttaksperiode uttaksperiode = Uttaksperiode.class.cast(periode);
            return new UttaksPeriode(
                    uttaksperiode.getFom(),
                    uttaksperiode.getTom(),
                    tilStønadKontoType(uttaksperiode.getType()),
                    uttaksperiode.isOenskerSamtidigUttak(),
                    tilMorsAktivitet(uttaksperiode.getMorsAktivitetIPerioden()),
                    uttaksperiode.isOenskerFlerbarnsdager(),
                    uttaksperiode.getSamtidigUttakProsent(),
                    emptyList());
        }
        throw new UnexpectedInputException("Ikke-støttet periode " + periode.getClass().getSimpleName());
    }

    private static Boolean tilBoolean(boolean value) {
        return Boolean.valueOf(value);
    }

    private static List<String> tilArbeidsgiver(Arbeidsgiver arbeidsgiver) {
        return Optional.ofNullable(arbeidsgiver)
                .map(Arbeidsgiver::getIdentifikator)
                .map(Collections::singletonList)
                .orElse(emptyList());
    }

    private static MorsAktivitet tilMorsAktivitet(MorsAktivitetsTyper morsAktivitetIPerioden) {
        if (morsAktivitetIPerioden == null || morsAktivitetIPerioden.getKode().equals(UKJENT_KODEVERKSVERDI)) {
            return null;
        }
        return MorsAktivitet.valueOf(morsAktivitetIPerioden.getKode());
    }

    private static StønadskontoType tilStønadKontoType(Uttaksperiodetyper type) {
        if (type == null || type.getKode().equals(UKJENT_KODEVERKSVERDI)) {
            return null;
        }
        return StønadskontoType.valueOf(type.getKode());
    }

    private static UtsettelsesÅrsak tilÅrsak(Utsettelsesaarsaker aarsak) {
        if (aarsak == null || aarsak.getKode().equals(UKJENT_KODEVERKSVERDI)) {
            return null;
        }
        return UtsettelsesÅrsak.valueOf(aarsak.getKode());
    }

    private static Oppholdsårsak tilÅrsak(Oppholdsaarsaker aarsak) {
        if (aarsak == null || aarsak.getKode().equals(UKJENT_KODEVERKSVERDI)) {
            return null;
        }
        return Oppholdsårsak.valueOf(aarsak.getKode());
    }

    private static no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad tilDekningsgrad(
            Dekningsgrad dekningsgrad) {
        if (dekningsgrad == null) {
            return null;
        }
        return no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad
                .fraKode(dekningsgrad.getDekningsgrad().getKode());
    }

    private no.nav.foreldrepenger.mottak.domain.felles.annenforelder.AnnenForelder tilAnnenForelder(
            AnnenForelder annenForelder) {
        if (annenForelder == null) {
            return null;
        }
        if (annenForelder instanceof UkjentForelder) {
            return new no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UkjentForelder();
        }
        if (annenForelder instanceof AnnenForelderMedNorskIdent) {
            AnnenForelderMedNorskIdent norskForelder = AnnenForelderMedNorskIdent.class.cast(annenForelder);
            return new NorskForelder(
                    oppslag.getFnr(new AktorId(norskForelder.getAktoerId())),
                    null);
        }
        if (annenForelder instanceof AnnenForelderUtenNorskIdent) {
            AnnenForelderUtenNorskIdent utenlandsForelder = AnnenForelderUtenNorskIdent.class.cast(annenForelder);
            return new UtenlandskForelder(
                    utenlandsForelder.getUtenlandskPersonidentifikator(),
                    tilLand(utenlandsForelder.getLand()),
                    null);
        }
        throw new UnexpectedInputException("Ikke-støttet annen forelder " + annenForelder.getClass().getSimpleName());
    }

    private static Søker tilSøker(Bruker søker) {
        return new Søker(BrukerRolle.valueOf(søker.getSoeknadsrolle().getKode()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapperEgenskaper=" + mapperEgenskaper() + "]";
    }
}
