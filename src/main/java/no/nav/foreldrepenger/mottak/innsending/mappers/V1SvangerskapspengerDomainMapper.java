package no.nav.foreldrepenger.mottak.innsending.mappers;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.SEND_SENERE;
import static no.nav.foreldrepenger.mottak.domain.felles.SpråkKode.defaultSpråk;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.SpråkKode;
import no.nav.foreldrepenger.mottak.domain.felles.ÅpenPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjeningType;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.EgenNæring;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.FrilansOppdrag;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Regnskapsfører;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Virksomhetstype;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.jaxb.SVPV1JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.OppholdUtlandet;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Periode;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.AnnenOpptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.EgenNaering;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Frilans;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Frilansoppdrag;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.NorskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Regnskapsfoerer;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.UtenlandskArbeidsforhold;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.UtenlandskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.AnnenOpptjeningTyper;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Brukerroller;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Innsendingstype;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Land;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Spraakkode;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Virksomhetstyper;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Arbeidsforhold;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.DelvisTilrettelegging;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Frilanser;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.HelTilrettelegging;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.IngenTilrettelegging;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.PrivatArbeidsgiver;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.SelvstendigNæringsdrivende;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Svangerskapspenger;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Tilrettelegging;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.TilretteleggingListe;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Virksomhet;
import no.nav.vedtak.felles.xml.soeknad.v3.OmYtelse;
import no.nav.vedtak.felles.xml.soeknad.v3.Soeknad;

@Component
public class V1SvangerskapspengerDomainMapper implements DomainMapper {

    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V1, INITIELL_SVANGERSKAPSPENGER);

    private static final SVPV1JAXBUtil JAXB = new SVPV1JAXBUtil();
    private static final Logger LOG = LoggerFactory.getLogger(V1SvangerskapspengerDomainMapper.class);

    private static final no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.ObjectFactory FP_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.ObjectFactory SVP_FACTORY_V1 = new no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.v3.ObjectFactory SØKNAD_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.v3.ObjectFactory();

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
        throw new UnexpectedInputException("Endringssøknad ikke støttet for svangerskapspenger");
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

    private static Spraakkode språkFra(Søker søker) {
        return Optional.ofNullable(søker)
                .map(Søker::getSpråkkode)
                .map(SpråkKode::name)
                .map(V1SvangerskapspengerDomainMapper::språkKodeFra)
                .orElse(defaultSpråkKode());
    }

    private static Spraakkode defaultSpråkKode() {
        return språkKodeFra(defaultSpråk());
    }

    private static Spraakkode språkKodeFra(SpråkKode kode) {
        return språkKodeFra(kode.name());
    }

    private static Spraakkode språkKodeFra(String kode) {
        return new Spraakkode().withKode(kode);
    }

    private static List<Vedlegg> vedleggFra(
            List<? extends no.nav.foreldrepenger.mottak.domain.felles.Vedlegg> vedlegg) {
        return safeStream(vedlegg)
                .map(V1SvangerskapspengerDomainMapper::vedleggFra)
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

    private static OmYtelse ytelseFra(Søknad søknad) {
        no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger ytelse = no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger.class
                .cast(søknad.getYtelse());
        return new OmYtelse().withAny(JAXB.marshalToElement(svangerskapsPengerFra(ytelse)));
    }

    private static JAXBElement<Svangerskapspenger> svangerskapsPengerFra(
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger ytelse) {
        return SVP_FACTORY_V1.createSvangerskapspenger(new Svangerskapspenger()
                .withTermindato(ytelse.getTermindato())
                .withFødselsdato(ytelse.getFødselsdato())
                .withOpptjening(opptjeningFra(ytelse.getOpptjening()))
                .withTilretteleggingListe(tilretteleggingFra(ytelse.getTilrettelegging()))
                .withMedlemskap(medlemskapFra(ytelse)));
    }

    private static Medlemskap medlemskapFra(
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger ytelse) {
        return medlemskapFra(ytelse.getMedlemsskap(),
                relasjonsDatoFra(ytelse.getTermindato(), ytelse.getFødselsdato()));
    }

    private static TilretteleggingListe tilretteleggingFra(
            List<no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging> tilrettelegginger) {
        return new TilretteleggingListe().withTilrettelegging(
                safeStream(tilrettelegginger)
                        .map(V1SvangerskapspengerDomainMapper::create)
                        .collect(toList()));
    }

    private static Tilrettelegging create(
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging tilrettelegging) {
        if (tilrettelegging instanceof no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging) {
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging ingen = no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging.class
                    .cast(tilrettelegging);
            return new IngenTilrettelegging()
                    .withVedlegg(tilretteleggingVedleggFraIDs(ingen.getVedlegg()))
                    .withSlutteArbeidFom(ingen.getSlutteArbeidFom())
                    .withBehovForTilretteleggingFom(ingen.getBehovForTilretteleggingFom())
                    .withArbeidsforhold(arbeidsforholdFra(ingen.getArbeidsforhold()));
        }
        if (tilrettelegging instanceof no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging) {
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging delvis = no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging.class
                    .cast(tilrettelegging);
            return new DelvisTilrettelegging()
                    .withVedlegg(tilretteleggingVedleggFraIDs(delvis.getVedlegg()))
                    .withTilrettelagtArbeidFom(delvis.getTilrettelagtArbeidFom())
                    .withStillingsprosent(BigDecimal.valueOf(delvis.getStillingsprosent().getProsent()))
                    .withBehovForTilretteleggingFom(delvis.getBehovForTilretteleggingFom())
                    .withArbeidsforhold(arbeidsforholdFra(delvis.getArbeidsforhold()));
        }
        if (tilrettelegging instanceof no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging) {
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging hel = no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging.class
                    .cast(tilrettelegging);
            return new HelTilrettelegging()
                    .withVedlegg(tilretteleggingVedleggFraIDs(hel.getVedlegg()))
                    .withTilrettelagtArbeidFom(hel.getTilrettelagtArbeidFom())
                    .withBehovForTilretteleggingFom(hel.getBehovForTilretteleggingFom())
                    .withArbeidsforhold(arbeidsforholdFra(hel.getArbeidsforhold()));
        }
        throw new UnexpectedInputException("Ukjent tilrettelegging " + tilrettelegging.getClass().getSimpleName());
    }

    private static List<JAXBElement<Object>> tilretteleggingVedleggFraIDs(List<String> vedlegg) {
        return safeStream(vedlegg)
                .map(s -> SVP_FACTORY_V1.createTilretteleggingVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static Arbeidsforhold arbeidsforholdFra(
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold forhold) {

        if (forhold instanceof no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet) {
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet virksomhet = no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet.class
                    .cast(forhold);
            return new Virksomhet()
                    .withIdentifikator(virksomhet.getOrgnr());
        }
        if (forhold instanceof no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver) {
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver privat = no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver.class
                    .cast(forhold);
            return new PrivatArbeidsgiver()
                    .withIdentifikator(privat.getFnr().getFnr());
        }

        if (forhold instanceof no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Frilanser) {
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Frilanser frilanser = no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Frilanser.class
                    .cast(forhold);
            return new Frilanser()
                    .withOpplysningerOmTilretteleggingstiltak(frilanser.getTilretteleggingstiltak())
                    .withOpplysningerOmRisikofaktorer(frilanser.getRisikoFaktorer());
        }

        if (forhold instanceof no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende) {
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende selvstendig = no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende.class
                    .cast(forhold);
            return new SelvstendigNæringsdrivende()
                    .withOpplysningerOmTilretteleggingstiltak(selvstendig.getTilretteleggingstiltak())
                    .withOpplysningerOmRisikofaktorer(selvstendig.getRisikoFaktorer());
        }

        throw new UnexpectedInputException("Ukjent arbeidsforhold " + forhold.getClass().getSimpleName());
    }

    private static no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.UtenlandskArbeidsforhold utenlandskArbeidsforholdFra(
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskArbeidsforhold forhold) {
        return new UtenlandskArbeidsforhold()
                .withVedlegg(utenlandsArbeidsforholdVedleggFra(forhold.getVedlegg()))
                .withArbeidsgiversnavn(forhold.getArbeidsgiverNavn())
                .withArbeidsland(landFra(forhold.getLand()))
                .withPeriode(periodeFra(forhold.getPeriode()));
    }

    private static List<JAXBElement<Object>> utenlandsArbeidsforholdVedleggFra(List<String> vedlegg) {
        return safeStream(vedlegg)
                .map(s -> FP_FACTORY_V3.createUtenlandskArbeidsforholdVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static Opptjening opptjeningFra(
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening opptjening) {
        return new Opptjening()
                .withUtenlandskArbeidsforhold(utenlandskeArbeidsforholdFra(opptjening.getUtenlandskArbeidsforhold()))
                .withFrilans(frilansFra(opptjening.getFrilans()))
                .withEgenNaering(egneNæringerFra(opptjening.getEgenNæring()))
                .withAnnenOpptjening(andreOpptjeningerFra(opptjening.getAnnenOpptjening()));
    }

    private static List<UtenlandskArbeidsforhold> utenlandskeArbeidsforholdFra(
            List<no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskArbeidsforhold> arbeidsforhold) {
        return safeStream(arbeidsforhold)
                .map(V1SvangerskapspengerDomainMapper::utenlandskArbeidsforholdFra)
                .collect(toList());
    }

    private static List<AnnenOpptjening> andreOpptjeningerFra(
            List<no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening> annenOpptjening) {
        return safeStream(annenOpptjening)
                .map(V1SvangerskapspengerDomainMapper::annenOpptjeningFra)
                .collect(toList());
    }

    private static AnnenOpptjening annenOpptjeningFra(
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening annen) {
        return Optional.ofNullable(annen)
                .map(V1SvangerskapspengerDomainMapper::create)
                .orElse(null);
    }

    private static AnnenOpptjening create(no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening annen) {
        return new AnnenOpptjening()
                .withVedlegg(annenOpptjeningVedleggFra(annen.getVedlegg()))
                .withType(annenOpptjeningTypeFra(annen.getType()))
                .withPeriode(periodeFra(annen.getPeriode()));
    }

    private static List<JAXBElement<Object>> annenOpptjeningVedleggFra(List<String> vedlegg) {
        return vedlegg.stream()
                .map(s -> FP_FACTORY_V3.createAnnenOpptjeningVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static AnnenOpptjeningTyper annenOpptjeningTypeFra(AnnenOpptjeningType type) {
        return Optional.ofNullable(type)
                .map(AnnenOpptjeningType::name)
                .map(V1SvangerskapspengerDomainMapper::create)
                .orElse(null);
    }

    private static AnnenOpptjeningTyper create(String kode) {
        AnnenOpptjeningTyper type = new AnnenOpptjeningTyper().withKode(kode);
        type.setKodeverk(type.getKodeverk());
        return type;
    }

    private static Frilans frilansFra(no.nav.foreldrepenger.mottak.domain.felles.opptjening.Frilans frilans) {
        return Optional.ofNullable(frilans)
                .map(V1SvangerskapspengerDomainMapper::create)
                .orElse(null);
    }

    private static Frilans create(no.nav.foreldrepenger.mottak.domain.felles.opptjening.Frilans frilans) {
        return new Frilans()
                .withVedlegg(frilansVedleggFraIDs(frilans.getVedlegg()))
                .withErNyoppstartet(frilans.isNyOppstartet())
                .withHarInntektFraFosterhjem(frilans.isHarInntektFraFosterhjem())
                .withNaerRelasjon(!CollectionUtils.isEmpty(frilans.getFrilansOppdrag()))
                .withPeriode(periodeFra(frilans.getPeriode()))
                .withFrilansoppdrag(frilansOppdragFra(frilans.getFrilansOppdrag()));
    }

    private static List<Frilansoppdrag> frilansOppdragFra(List<FrilansOppdrag> frilansOppdrag) {
        return safeStream(frilansOppdrag)
                .map(V1SvangerskapspengerDomainMapper::frilansOppdragFra)
                .collect(toList());
    }

    private static Frilansoppdrag frilansOppdragFra(FrilansOppdrag oppdrag) {
        return Optional.ofNullable(oppdrag)
                .map(V1SvangerskapspengerDomainMapper::create)
                .orElse(null);
    }

    private static Frilansoppdrag create(FrilansOppdrag oppdrag) {
        return new Frilansoppdrag()
                .withOppdragsgiver(oppdrag.getOppdragsgiver())
                .withPeriode(periodeFra(oppdrag.getPeriode()));
    }

    private static List<JAXBElement<Object>> frilansVedleggFraIDs(List<String> vedlegg) {
        return safeStream(vedlegg)
                .map(s -> FP_FACTORY_V3.createFrilansVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
    }

    private static List<EgenNaering> egneNæringerFra(List<EgenNæring> egneNæringer) {
        return safeStream(egneNæringer)
                .map(V1SvangerskapspengerDomainMapper::create)
                .collect(toList());
    }

    private static EgenNaering create(EgenNæring egenNæring) {
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
        throw new UnexpectedInputException("Ukjent egen næring " + egenNæring.getClass().getSimpleName());
    }

    private static List<JAXBElement<Object>> egenNæringVedleggFraIDs(List<String> vedlegg) {
        return safeStream(vedlegg)
                .map(s -> FP_FACTORY_V3.createEgenNaeringVedlegg(new Vedlegg().withId(s)))
                .collect(toList());
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

    private static Periode periodeFra(ÅpenPeriode periode) {
        return Optional.ofNullable(periode)
                .map(p -> new Periode()
                        .withFom(p.getFom())
                        .withTom(p.getTom()))
                .orElse(null);
    }

    private static List<Virksomhetstyper> virksomhetsTyperFra(List<Virksomhetstype> typer) {
        return safeStream(typer)
                .map(V1SvangerskapspengerDomainMapper::virksomhetsTypeFra)
                .collect(toList());
    }

    private static Virksomhetstyper virksomhetsTypeFra(Virksomhetstype type) {
        return Optional.ofNullable(type)
                .map(Virksomhetstype::name)
                .map(V1SvangerskapspengerDomainMapper::virksomhetsTypeFra)
                .orElse(null);
    }

    private static Virksomhetstyper virksomhetsTypeFra(String type) {
        Virksomhetstyper vt = new Virksomhetstyper().withKode(type);
        vt.setKodeverk(vt.getKodeverk());
        return vt;
    }

    private static LocalDate relasjonsDatoFra(LocalDate termindato, LocalDate fødselsdato) {
        return Optional.ofNullable(fødselsdato)
                .orElse(termindato);
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

    private static Medlemskap medlemskapFra(Medlemsskap ms, LocalDate relasjonsDato) {
        return Optional.ofNullable(ms)
                .map(m -> create(ms, relasjonsDato))
                .orElse(null);
    }

    private static Medlemskap create(Medlemsskap ms, LocalDate relasjonsDato) {
        return new Medlemskap()
                .withOppholdUtlandet(oppholdUtlandetFra(ms))
                .withINorgeVedFoedselstidspunkt(ms.varINorge(relasjonsDato))
                .withBoddINorgeSiste12Mnd(oppholdINorgeSiste12(ms))
                .withBorINorgeNeste12Mnd(oppholdINorgeNeste12(ms));
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
                .map(V1SvangerskapspengerDomainMapper::utenlandOppholdFra)
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

    private static Land landFra(CountryCode land) {
        return Optional.ofNullable(land)
                .map(s -> landFra(s.getAlpha3()))
                .orElse(null);
    }

    private static Land landFra(String alphq3) {
        Land land = new Land().withKode(alphq3);
        return land.withKodeverk(land.getKodeverk());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapperEgenskaper=" + mapperEgenskaper() + "]";
    }

}
