package no.nav.foreldrepenger.mottak.innsending.mappers;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.*;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.*;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.*;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.*;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.*;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.errorhandling.VersionMismatchException;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.foreldrepenger.mottak.util.jaxb.FPV2JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.Endringssoeknad;
import no.nav.vedtak.felles.xml.soeknad.felles.v2.AnnenForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v2.Rettigheter;
import no.nav.vedtak.felles.xml.soeknad.felles.v2.UkjentForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v2.Vedlegg;
import no.nav.vedtak.felles.xml.soeknad.felles.v2.*;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.AnnenOpptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.Dekningsgrad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.Frilans;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.NorskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.UtenlandskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.*;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v2.*;
import no.nav.vedtak.felles.xml.soeknad.uttak.v2.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v2.Person;
import no.nav.vedtak.felles.xml.soeknad.uttak.v2.*;
import no.nav.vedtak.felles.xml.soeknad.v2.OmYtelse;
import no.nav.vedtak.felles.xml.soeknad.v2.Soeknad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.neovisionaries.i18n.CountryCode.XK;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.SEND_SENERE;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;

@Component
public class V2ForeldrepengerDomainMapper implements DomainMapper {

    private static final Land KOSOVO = landFra("XXK");

    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V2, ENDRING_FORELDREPENGER,
            INITIELL_FORELDREPENGER);

    private static final FPV2JAXBUtil JAXB = new FPV2JAXBUtil();
    private static final Logger LOG = LoggerFactory.getLogger(V2ForeldrepengerDomainMapper.class);

    private static final no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.ObjectFactory FP_FACTORY_V2 = new no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.felles.v2.ObjectFactory FELLES_FACTORY_V2 = new no.nav.vedtak.felles.xml.soeknad.felles.v2.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.v2.ObjectFactory SØKNAD_FACTORY_V2 = new no.nav.vedtak.felles.xml.soeknad.v2.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.uttak.v2.ObjectFactory UTTAK_FACTORY_V2 = new no.nav.vedtak.felles.xml.soeknad.uttak.v2.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.ObjectFactory ENDRING_FACTORY_V2 = new no.nav.vedtak.felles.xml.soeknad.endringssoeknad.v2.ObjectFactory();

    private final Oppslag oppslag;

    public V2ForeldrepengerDomainMapper(Oppslag oppslag) {
        this.oppslag = oppslag;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return EGENSKAPER;
    }

    @Override
    public String tilXML(Søknad søknad, AktorId søker, SøknadEgenskap egenskap) {
        return JAXB.marshal(SØKNAD_FACTORY_V2.createSoeknad(tilModell(søknad, søker)));
    }

    @Override
    public String tilXML(Endringssøknad endringssøknad, AktorId søker, SøknadEgenskap egenskap) {
        return JAXB.marshal(SØKNAD_FACTORY_V2.createSoeknad(tilModell(endringssøknad, søker)));
    }

    private Soeknad tilModell(Endringssøknad endringsøknad, AktorId søker) {
        LOG.debug(CONFIDENTIAL, "Genererer endringssøknad XML fra {}", endringsøknad);
        return new Soeknad()
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
        return ENDRING_FACTORY_V2.createEndringssoeknad(new Endringssoeknad()
                .withFordeling(fordelingFra(endringssøknad))
                .withSaksnummer(endringssøknad.getSaksnr()));
    }

    public Soeknad tilModell(Søknad søknad, AktorId søker) {
        LOG.debug(CONFIDENTIAL, "Genererer søknad XML fra {}", søknad);
        return new Soeknad()
                .withAndreVedlegg(vedleggFra(søknad.getFrivilligeVedlegg()))
                .withPaakrevdeVedlegg(vedleggFra(søknad.getPåkrevdeVedlegg()))
                .withSoeker(søkerFra(søker, søknad.getSøker()))
                .withOmYtelse(ytelseFra(søknad))
                .withMottattDato(søknad.getMottattdato().toLocalDate())
                .withBegrunnelseForSenSoeknad(søknad.getBegrunnelseForSenSøknad())
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger());
    }

    private static List<Vedlegg> vedleggFra(
            List<? extends no.nav.foreldrepenger.mottak.domain.felles.Vedlegg> vedlegg) {
        return safeStream(vedlegg)
                .map(V2ForeldrepengerDomainMapper::vedleggFra)
                .collect(toList());
    }

    private static Vedlegg vedleggFra(no.nav.foreldrepenger.mottak.domain.felles.Vedlegg vedlegg) {
        return new Vedlegg()
                .withId(vedlegg.getId())
                .withTilleggsinformasjon(vedlegg.getBeskrivelse())
                .withSkjemanummer(vedlegg.getDokumentType().name())
                .withInnsendingstype(innsendingstypeFra(vedlegg.getInnsendingsType()));
    }

    private static Innsendingstype innsendingstypeFra(InnsendingsType innsendingsType) {

        switch (innsendingsType) {
        case SEND_SENERE:
            return innsendingsTypeMedKodeverk(SEND_SENERE);
        case LASTET_OPP:
            return innsendingsTypeMedKodeverk(LASTET_OPP);
        default:
            throw new UnexpectedInputException("Innsendingstype " + innsendingsType + " foreløpig kke støttet");
        }
    }

    private static Innsendingstype innsendingsTypeMedKodeverk(InnsendingsType type) {
        Innsendingstype typeMedKodeverk = new Innsendingstype().withKode(type.name());
        return typeMedKodeverk.withKodeverk(typeMedKodeverk.getKodeverk());
    }

    private OmYtelse ytelseFra(Søknad søknad) {
        no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger ytelse = no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.class
                .cast(søknad.getYtelse());
        LOG.debug(CONFIDENTIAL, "Genererer ytelse XML fra {}", ytelse);
        return new OmYtelse().withAny(JAXB.marshalToElement(foreldrePengerFra(ytelse)));
    }

    private JAXBElement<Foreldrepenger> foreldrePengerFra(
            no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger ytelse) {
        return FP_FACTORY_V2.createForeldrepenger(new Foreldrepenger()
                .withDekningsgrad(dekningsgradFra(ytelse.getDekningsgrad()))
                .withMedlemskap(medlemsskapFra(ytelse.getMedlemsskap(), ytelse.getRelasjonTilBarn()))
                .withOpptjening(opptjeningFra(ytelse.getOpptjening()))
                .withFordeling(fordelingFra(ytelse.getFordeling()))
                .withRettigheter(
                        rettigheterFra(ytelse.getRettigheter(), erAnnenForelderUkjent(ytelse.getAnnenForelder())))
                .withAnnenForelder(annenForelderFra(ytelse.getAnnenForelder()))
                .withRelasjonTilBarnet(relasjonFra(ytelse.getRelasjonTilBarn())));
    }

    private Fordeling fordelingFra(Endringssøknad endringssøknad) {
        no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger ytelse = no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.class
                .cast(endringssøknad.getYtelse());
        LOG.debug(CONFIDENTIAL, "Genererer fordeling endringssøknad XML fra {}", ytelse.getFordeling());
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

    private Opptjening opptjeningFra(no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening opptjening) {
        if (opptjening == null) {
            return null;
        }
        LOG.debug(CONFIDENTIAL, "Genererer opptjening XML fra {}", opptjening);
        return new Opptjening()
                .withFrilans(frilansFra(opptjening.getFrilans()))
                .withEgenNaering(egenNæringFra(opptjening.getEgenNæring()))
                .withUtenlandskArbeidsforhold(utenlandskArbeidsforholdFra(opptjening.getUtenlandskArbeidsforhold()))
                .withAnnenOpptjening(annenOpptjeningFra(opptjening.getAnnenOpptjening()));
    }

    private static Frilans frilansFra(no.nav.foreldrepenger.mottak.domain.felles.opptjening.Frilans frilans) {
        if (frilans == null) {
            return null;
        }
        LOG.debug(CONFIDENTIAL, "Genererer frilans XML fra {}", frilans);

        return new Frilans()
                .withVedlegg(frilansVedleggFraIDs(frilans.getVedlegg()))
                .withErNyoppstartet(frilans.isNyOppstartet())
                .withHarInntektFraFosterhjem(frilans.isHarInntektFraFosterhjem())
                .withNaerRelasjon(!CollectionUtils.isEmpty(frilans.getFrilansOppdrag()))
                .withPeriode(periodeFra(frilans.getPeriode()))
                .withFrilansoppdrag(frilansOppdragFra(frilans.getFrilansOppdrag()));
    }

    private static List<JAXBElement<Object>> frilansVedleggFraIDs(List<String> vedlegg) {
        return vedlegg.stream()
                .map(s -> FP_FACTORY_V2.createFrilansVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static List<Frilansoppdrag> frilansOppdragFra(List<FrilansOppdrag> frilansOppdrag) {
        return safeStream(frilansOppdrag)
                .map(V2ForeldrepengerDomainMapper::frilansOppdragFra)
                .collect(toList());
    }

    private static Frilansoppdrag frilansOppdragFra(FrilansOppdrag frilansOppdrag) {
        return new Frilansoppdrag()
                .withOppdragsgiver(frilansOppdrag.getOppdragsgiver())
                .withPeriode(periodeFra(frilansOppdrag.getPeriode()));
    }

    private static List<EgenNaering> egenNæringFra(List<EgenNæring> egenNæring) {
        return safeStream(egenNæring)
                .map(V2ForeldrepengerDomainMapper::egenNæringFra)
                .collect(toList());
    }

    private static List<no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.UtenlandskArbeidsforhold> utenlandskArbeidsforholdFra(
            List<UtenlandskArbeidsforhold> utenlandskArbeidsforhold) {
        return safeStream(utenlandskArbeidsforhold)
                .map(V2ForeldrepengerDomainMapper::utenlandskArbeidsforholdFra)
                .collect(toList());
    }

    private List<AnnenOpptjening> annenOpptjeningFra(
            List<no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening> annenOpptjening) {
        return safeStream(annenOpptjening)
                .map(this::annenOpptjeningFra)
                .collect(toList());
    }

    private static EgenNaering egenNæringFra(
            EgenNæring egenNæring) {
        LOG.debug(CONFIDENTIAL, "Genererer egenNæring XML fra {}", egenNæring);

        if (egenNæring instanceof no.nav.foreldrepenger.mottak.domain.felles.opptjening.NorskOrganisasjon) {
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.NorskOrganisasjon norskOrg = no.nav.foreldrepenger.mottak.domain.felles.opptjening.NorskOrganisasjon.class
                    .cast(egenNæring);
            return new NorskOrganisasjon()
                    .withVedlegg(egenNæringVedleggFraIDs(norskOrg.getVedlegg()))
                    .withBeskrivelseAvEndring(norskOrg.getBeskrivelseEndring())
                    .withNaerRelasjon(norskOrg.isNærRelasjon())
                    .withEndringsDato(norskOrg.getEndringsDato())
                    .withOppstartsdato(norskOrg.getOppstartsDato())
                    .withErNyoppstartet(norskOrg.isErNyOpprettet())
                    .withErNyIArbeidslivet(norskOrg.isErNyIArbeidslivet())
                    .withErVarigEndring(norskOrg.isErVarigEndring())
                    .withNaeringsinntektBrutto(BigInteger.valueOf(norskOrg.getNæringsinntektBrutto()))
                    .withNavn(norskOrg.getOrgName())
                    .withOrganisasjonsnummer(norskOrg.getOrgNummer())
                    .withPeriode(periodeFra(norskOrg.getPeriode()))
                    .withRegnskapsfoerer(regnskapsFørerFra(norskOrg.getRegnskapsførere()))
                    .withVirksomhetstype(virksomhetsTyperFra(norskOrg.getVirksomhetsTyper()))
                    .withOppstartsdato(norskOrg.getOppstartsDato());
        }
        if (egenNæring instanceof no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskOrganisasjon) {
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskOrganisasjon utenlandskOrg = no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskOrganisasjon.class
                    .cast(egenNæring);
            return new UtenlandskOrganisasjon()
                    .withVedlegg(egenNæringVedleggFraIDs(utenlandskOrg.getVedlegg()))
                    .withBeskrivelseAvEndring(utenlandskOrg.getBeskrivelseEndring())
                    .withNaerRelasjon(utenlandskOrg.isNærRelasjon())
                    .withEndringsDato(utenlandskOrg.getEndringsDato())
                    .withOppstartsdato(utenlandskOrg.getOppstartsDato())
                    .withErNyoppstartet(utenlandskOrg.isErNyOpprettet())
                    .withErNyIArbeidslivet(utenlandskOrg.isErNyIArbeidslivet())
                    .withErVarigEndring(utenlandskOrg.isErVarigEndring())
                    .withNaeringsinntektBrutto(BigInteger.valueOf(utenlandskOrg.getNæringsinntektBrutto()))
                    .withNavn(utenlandskOrg.getOrgName())
                    .withRegistrertILand(landFra(utenlandskOrg.getRegistrertILand()))
                    .withPeriode(periodeFra(utenlandskOrg.getPeriode()))
                    .withRegnskapsfoerer(regnskapsFørerFra(utenlandskOrg.getRegnskapsførere()))
                    .withVirksomhetstype(virksomhetsTyperFra(utenlandskOrg.getVirksomhetsTyper()));
        }
        throw new UnexpectedInputException("Vil aldri skje");
    }

    private static List<JAXBElement<Object>> egenNæringVedleggFraIDs(List<String> vedlegg) {
        return vedlegg.stream()
                .map(s -> FP_FACTORY_V2.createEgenNaeringVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static List<Virksomhetstyper> virksomhetsTyperFra(
            List<Virksomhetstype> typer) {
        return safeStream(typer)
                .map(V2ForeldrepengerDomainMapper::virksomhetsTypeFra)
                .collect(toList());
    }

    private static Virksomhetstyper virksomhetsTypeFra(
            Virksomhetstype type) {
        return Optional.ofNullable(type)
                .map(s -> virksomhetsTypeFra(s.name()))
                .orElse(null);
    }

    private static Virksomhetstyper virksomhetsTypeFra(String type) {
        Virksomhetstyper vt = new Virksomhetstyper().withKode(type);
        vt.setKodeverk(vt.getKodeverk());
        return vt;
    }

    private static Regnskapsfoerer regnskapsFørerFra(List<Regnskapsfører> regnskapsførere) {
        if (CollectionUtils.isEmpty(regnskapsførere)) {
            return null;
        }
        if (regnskapsførere.size() > 1) {
            LOG.warn("Flere regnskapsførere ikke støttet, bruker kun den første");
        }
        Regnskapsfører regnskapsfører = regnskapsførere.get(0);
        return new Regnskapsfoerer()
                .withTelefon(regnskapsfører.getTelefon())
                .withNavn(regnskapsfører.getNavn());
    }

    private AnnenOpptjening annenOpptjeningFra(
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening annenOpptjening) {
        LOG.debug(CONFIDENTIAL, "Genererer annen opptjening XML fra {}", annenOpptjening);

        return new AnnenOpptjening()
                .withVedlegg(annenOpptjeningVedleggFra(annenOpptjening.getVedlegg()))
                .withType(annenOpptjeningTypeFra(annenOpptjening.getType()))
                .withPeriode(periodeFra(annenOpptjening.getPeriode()));
    }

    private static List<JAXBElement<Object>> annenOpptjeningVedleggFra(List<String> vedlegg) {
        return vedlegg.stream()
                .map(s -> FP_FACTORY_V2.createAnnenOpptjeningVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.UtenlandskArbeidsforhold utenlandskArbeidsforholdFra(
            UtenlandskArbeidsforhold arbeidsForhold) {
        return utenlandskArbeidsforhold(arbeidsForhold);

    }

    private static no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.UtenlandskArbeidsforhold utenlandskArbeidsforhold(
            UtenlandskArbeidsforhold arbeidsForhold) {
        LOG.debug(CONFIDENTIAL, "Genererer utenlands arbeidsforhold XML fra {}", arbeidsForhold);
        return new no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v2.UtenlandskArbeidsforhold()
                .withVedlegg(utenlandsArbeidsforholdVedleggFra(arbeidsForhold.getVedlegg()))
                .withArbeidsgiversnavn(arbeidsForhold.getArbeidsgiverNavn())
                .withArbeidsland(landFra(arbeidsForhold.getLand()))
                .withPeriode(periodeFra(arbeidsForhold.getPeriode()));
    }

    private static List<JAXBElement<Object>> utenlandsArbeidsforholdVedleggFra(List<String> vedlegg) {
        return vedlegg.stream()
                .map(s -> FP_FACTORY_V2.createUtenlandskArbeidsforholdVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static AnnenOpptjeningTyper annenOpptjeningTypeFra(AnnenOpptjeningType type) {
        if (!lovligVerdiForVersjon(type)) {
            throw new VersionMismatchException(type.getClass().getSimpleName(), type.name(), Versjon.V2,
                    lovligeVerdier());
        }
        return Optional.ofNullable(type)
                .map(AnnenOpptjeningType::name)
                .map(V2ForeldrepengerDomainMapper::annenOpptjeningTypeFra)
                .orElse(null);
    }

    private static String lovligeVerdier() {
        return Arrays.stream(AnnenOpptjeningType.values())
                .filter(a -> lovligVerdiForVersjon(a))
                .map(s -> s.name())
                .collect(Collectors.joining(","));
    }

    private static boolean lovligVerdiForVersjon(AnnenOpptjeningType type) {
        return type.versjoner.contains(V2);
    }

    private static AnnenOpptjeningTyper annenOpptjeningTypeFra(String kode) {
        AnnenOpptjeningTyper type = new AnnenOpptjeningTyper().withKode(kode);
        type.setKodeverk(type.getKodeverk());
        return type;
    }

    private static Periode periodeFra(ÅpenPeriode periode) {
        return Optional.ofNullable(periode)
                .map(s -> new Periode().withFom(s.getFom()).withTom(s.getTom()))
                .orElse(null);
    }

    private static Medlemskap medlemsskapFra(Medlemsskap ms, RelasjonTilBarn relasjon) {
        if (ms != null) {
            LOG.debug(CONFIDENTIAL, "Genererer medlemsskap XML fra {}", ms);
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
                .map(V2ForeldrepengerDomainMapper::utenlandOppholdFra)
                .collect(toList());
    }

    private static OppholdUtlandet utenlandOppholdFra(Utenlandsopphold opphold) {
        return opphold == null ? null
                : new OppholdUtlandet()
                        .withPeriode(new Periode()
                                .withFom(opphold.getVarighet().getFom())
                                .withTom(opphold.getVarighet().getTom()))
                        .withLand(landFra(opphold.getLand()));
    }

    private static Land landFra(CountryCode land) {
        if (XK.equals(land)) {
            return KOSOVO; // https://jira.adeo.no/browse/PFP-6077
        }
        return Optional.ofNullable(land)
                .map(s -> landFra(s.getAlpha3()))
                .orElse(null);
    }

    private static Land landFra(String alpha3) {
        Land land = new Land().withKode(alpha3);
        return land.withKodeverk(land.getKodeverk());
    }

    private Fordeling fordelingFra(no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Fordeling fordeling) {
        LOG.debug(CONFIDENTIAL, "Genererer fordeling XML fra {}", fordeling);
        if (fordeling == null) {
            return null;
        }
        return new Fordeling()
                .withPerioder(perioderFra(fordeling.getPerioder()))
                .withOenskerKvoteOverfoert(valgfriOverføringsÅrsakFra(fordeling.getØnskerKvoteOverført()))
                .withAnnenForelderErInformert(fordeling.isErAnnenForelderInformert());
    }

    private List<no.nav.vedtak.felles.xml.soeknad.uttak.v2.LukketPeriodeMedVedlegg> perioderFra(
            List<LukketPeriodeMedVedlegg> perioder) {
        return safeStream(perioder)
                .map(this::lukkerPeriodeFra)
                .collect(toList());

    }

    private static List<JAXBElement<Object>> lukketPeriodeVedleggFra(List<String> vedlegg) {
        return vedlegg.stream()
                .map(s -> UTTAK_FACTORY_V2.createLukketPeriodeMedVedleggVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private no.nav.vedtak.felles.xml.soeknad.uttak.v2.LukketPeriodeMedVedlegg lukkerPeriodeFra(
            LukketPeriodeMedVedlegg periode) {
        LOG.debug(CONFIDENTIAL, "Genererer periode XML fra {}", periode);
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
            LOG.debug("Annen forelder er ukjent, avleder verdier for rettigheter");
            return new Rettigheter()
                    .withHarOmsorgForBarnetIPeriodene(true)
                    .withHarAnnenForelderRett(false)
                    .withHarAleneomsorgForBarnet(true);
        }
        if (rettigheter == null) {
            return null;
        }
        LOG.debug(CONFIDENTIAL, "Genererer rettigheter XML fra {}", rettigheter);
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
        return new no.nav.vedtak.felles.xml.soeknad.felles.v2.UkjentForelder();
    }

    private static AnnenForelderUtenNorskIdent utenlandskForelder(UtenlandskForelder utenlandskForelder) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v2.AnnenForelderUtenNorskIdent()
                .withUtenlandskPersonidentifikator(utenlandskForelder.getId())
                .withLand(landFra(utenlandskForelder.getLand()));
    }

    private AnnenForelderMedNorskIdent norskForelder(NorskForelder norskForelder) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v2.AnnenForelderMedNorskIdent()
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
            return new no.nav.vedtak.felles.xml.soeknad.felles.v2.Adopsjon()
                    .withVedlegg(relasjonTilBarnVedleggFra(relasjonTilBarn.getVedlegg()))
                    .withAntallBarn(adopsjon.getAntallBarn())
                    .withFoedselsdato(adopsjon.getFødselsdato())
                    .withOmsorgsovertakelsesdato(adopsjon.getOmsorgsovertakelsesdato())
                    .withAdopsjonAvEktefellesBarn(adopsjon.isEktefellesBarn())
                    .withAnkomstdato(adopsjon.getAnkomstDato());
        }
        if (relasjonTilBarn instanceof Omsorgsovertakelse) {
            Omsorgsovertakelse omsorgsovertakelse = Omsorgsovertakelse.class.cast(relasjonTilBarn);
            return new no.nav.vedtak.felles.xml.soeknad.felles.v2.Omsorgsovertakelse()
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
                .map(s -> FELLES_FACTORY_V2.createSoekersRelasjonTilBarnetVedlegg(new Vedlegg().withId(s)))
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
