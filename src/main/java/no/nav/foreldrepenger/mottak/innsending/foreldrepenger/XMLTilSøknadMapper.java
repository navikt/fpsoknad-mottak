package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.Jaxb.context;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.util.List;

import javax.xml.bind.JAXBContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenOpptjeningType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.EgenNæring;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.FrilansOppdrag;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.GradertUttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.MorsAktivitet;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OppholdsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Oppholdsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OverføringsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Overføringsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Regnskapsfører;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.RelasjonTilBarnMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.StønadskontoType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtsettelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Virksomhetstype;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ÅpenPeriode;
import no.nav.foreldrepenger.mottak.http.Oppslag;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelderMedNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelderUtenNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Foedsel;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Periode;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Rettigheter;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.SoekersRelasjonTilBarnet;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Termin;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.UkjentForelder;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.AnnenOpptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Dekningsgrad;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.EgenNaering;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Frilans;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Frilansoppdrag;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.NorskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Opptjening;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.Regnskapsfoerer;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.UtenlandskOrganisasjon;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Land;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.MorsAktivitetsTyper;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Oppholdsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Overfoeringsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Utsettelsesaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Uttaksperiodetyper;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Virksomhetstyper;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Gradering;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Oppholdsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Overfoeringsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Utsettelsesperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v1.Uttaksperiode;
import no.nav.vedtak.felles.xml.soeknad.v1.OmYtelse;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

@Component
public class XMLTilSøknadMapper {

    private static final String UKJENT_KODEVERKSVERDI = "-";

    private static final Logger LOG = LoggerFactory.getLogger(XMLTilSøknadMapper.class);

    private static final JAXBContext CONTEXT = context(Soeknad.class, Foreldrepenger.class);

    private final Oppslag oppslag;

    public XMLTilSøknadMapper(Oppslag oppslag) {
        this.oppslag = oppslag;
    }

    public Søknad tilSøknad(String xml) {
        /*
         * Soeknad søknad = unmarshalToElement(xml, CONTEXT, Soeknad.class).getValue();
         * 
         * Søknad s = new Søknad(søknad.getMottattDato().atStartOfDay(),
         * tilSøker(søknad.getSoeker()), tilYtelse(søknad.getOmYtelse()));
         * s.setTilleggsopplysninger(søknad.getTilleggsopplysninger());
         * s.setBegrunnelseForSenSøknad(søknad.getBegrunnelseForSenSoeknad()); return s;
         */
        return null; // TODO for nw
    }

    private no.nav.foreldrepenger.mottak.domain.Ytelse tilYtelse(OmYtelse omYtelse) {
        if (omYtelse == null || omYtelse.getAny() == null || omYtelse.getAny().isEmpty()) {
            LOG.warn("Ingen ytelse i søknaden");
            return null;
        }
        if (omYtelse.getAny().size() > 1) {
            LOG.warn("Fikk {} ytelser i søknaden, forventet kun 1", omYtelse.getAny().size());
        }
        Object førsteYtelse = omYtelse.getAny().get(0);
        if (!(førsteYtelse instanceof Foreldrepenger)) {
            LOG.warn("Søknad av type {} er ikke støttet", førsteYtelse.getClass().getSimpleName());
            return null;
        }

        Foreldrepenger foreldrepengeSøknad = Foreldrepenger.class.cast(førsteYtelse);
        return no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger.builder()
                .annenForelder(tilAnnenForelder(foreldrepengeSøknad.getAnnenForelder()))
                .dekningsgrad(tilDekningsgrad(foreldrepengeSøknad.getDekningsgrad()))
                .fordeling(tilFordeling(foreldrepengeSøknad.getFordeling()))
                .medlemsskap(tilMedlemsskap(foreldrepengeSøknad.getMedlemskap()))
                .opptjening(tilOpptjening(foreldrepengeSøknad.getOpptjening()))
                .relasjonTilBarn(tilRelasjonTilBarn(foreldrepengeSøknad.getRelasjonTilBarnet()))
                .rettigheter(tilRettigheter(foreldrepengeSøknad.getRettigheter()))
                .build();

    }

    private static no.nav.foreldrepenger.mottak.domain.foreldrepenger.Rettigheter tilRettigheter(
            Rettigheter rettigheter) {
        if (rettigheter == null) {
            return null;
        }
        LOG.debug("Genererer rettigheter modell");

        return new no.nav.foreldrepenger.mottak.domain.foreldrepenger.Rettigheter(rettigheter.isHarAnnenForelderRett(),
                rettigheter.isHarOmsorgForBarnetIPeriodene(), rettigheter.isHarAleneomsorgForBarnet(), null);
    }

    private static RelasjonTilBarnMedVedlegg tilRelasjonTilBarn(SoekersRelasjonTilBarnet relasjonTilBarnet) {
        if (relasjonTilBarnet == null) {
            return null;
        }
        LOG.debug("Genererer relasjon til barn modell");

        if (relasjonTilBarnet instanceof Foedsel) {
            Foedsel fødsel = Foedsel.class.cast(relasjonTilBarnet);
            return new Fødsel(fødsel.getAntallBarn(), fødsel.getFoedselsdato());
        }
        if (relasjonTilBarnet instanceof Termin) {
            Termin termin = Termin.class.cast(relasjonTilBarnet);
            return new FremtidigFødsel(termin.getAntallBarn(), termin.getTermindato(), termin.getUtstedtdato(),
                    emptyList());

        }
        if (relasjonTilBarnet instanceof no.nav.vedtak.felles.xml.soeknad.felles.v1.Adopsjon) {
            no.nav.vedtak.felles.xml.soeknad.felles.v1.Adopsjon adopsjon = no.nav.vedtak.felles.xml.soeknad.felles.v1.Adopsjon.class
                    .cast(relasjonTilBarnet);
            return new Adopsjon(adopsjon.getAntallBarn(), adopsjon.getOmsorgsovertakelsesdato(),
                    adopsjon.isAdopsjonAvEktefellesBarn(), emptyList(), adopsjon.getAnkomstdato(),
                    adopsjon.getFoedselsdato());
        }
        throw new IllegalArgumentException("Ikke"
                + " støttet type " + relasjonTilBarnet.getClass().getSimpleName());
    }

    private static no.nav.foreldrepenger.mottak.domain.foreldrepenger.Opptjening tilOpptjening(Opptjening opptjening) {
        if (opptjening == null) {
            return null;
        }
        LOG.debug("Genererer opptjening modell");

        return new no.nav.foreldrepenger.mottak.domain.foreldrepenger.Opptjening(
                tilUtenlandsArbeidsforhold(opptjening.getUtenlandskArbeidsforhold()),
                tilEgenNæring(opptjening.getEgenNaering()), tilAnnenOpptjening(opptjening.getAnnenOpptjening()),
                tilFrilans(opptjening.getFrilans()));
    }

    private static no.nav.foreldrepenger.mottak.domain.foreldrepenger.Frilans tilFrilans(Frilans frilans) {
        if (frilans == null) {
            return null;
        }
        LOG.debug("Genererer frilans  modell");
        return new no.nav.foreldrepenger.mottak.domain.foreldrepenger.Frilans(tilÅpenPeriode(frilans.getPeriode()),
                frilans.isHarInntektFraFosterhjem(),
                frilans.isErNyoppstartet(),
                tilFrilansOppdrag(frilans.getFrilansoppdrag()),
                emptyList());
    }

    private static ÅpenPeriode tilÅpenPeriode(List<Periode> periode) {
        return periode == null || periode.isEmpty() ? null : tilÅpenPeriode(periode.get(0));
    }

    private static List<FrilansOppdrag> tilFrilansOppdrag(List<Frilansoppdrag> frilansoppdrag) {
        return safeStream(frilansoppdrag).map(XMLTilSøknadMapper::tilFrilansOppdrag).collect(toList());
    }

    private static FrilansOppdrag tilFrilansOppdrag(Frilansoppdrag frilansoppdrag) {
        if (frilansoppdrag == null) {
            return null;
        }
        return new FrilansOppdrag(frilansoppdrag.getOppdragsgiver(), tilÅpenPeriode(frilansoppdrag.getPeriode()));
    }

    private static ÅpenPeriode tilÅpenPeriode(Periode periode) {
        if (periode == null) {
            return null;
        }
        LOG.debug("Genererer åpen periode modell");
        return new ÅpenPeriode(periode.getFom(), periode.getTom());
    }

    private static List<no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenOpptjening> tilAnnenOpptjening(
            List<AnnenOpptjening> annenOpptjening) {
        return safeStream(annenOpptjening).map(XMLTilSøknadMapper::tilAnnenOpptjening).collect(toList());
    }

    private static no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenOpptjening tilAnnenOpptjening(
            AnnenOpptjening annenOpptjening) {
        if (annenOpptjening == null) {
            return null;
        }
        LOG.debug("Genererer annen opptjening modell");

        return new no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenOpptjening(
                AnnenOpptjeningType.valueOf(annenOpptjening.getType().getKode()),
                tilÅpenPeriode(annenOpptjening.getPeriode()), emptyList());
    }

    private static List<EgenNæring> tilEgenNæring(List<EgenNaering> egenNaering) {
        return safeStream(egenNaering).map(XMLTilSøknadMapper::tilEgenNæring).collect(toList());
    }

    private static EgenNæring tilEgenNæring(EgenNaering egenNæring) {
        if (egenNæring == null) {
            return null;
        }
        LOG.debug("Genererer egen næring modell");

        if (egenNæring instanceof NorskOrganisasjon) {
            NorskOrganisasjon norskOrg = NorskOrganisasjon.class.cast(egenNæring);
            return no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskOrganisasjon.builder()
                    .beskrivelseEndring(norskOrg.getBeskrivelseAvEndring())
                    .endringsDato(norskOrg.getEndringsDato())
                    .erNyOpprettet(norskOrg.isErNyoppstartet())
                    .erVarigEndring(norskOrg.isErVarigEndring())
                    .erNyIArbeidslivet(norskOrg.isErNyIArbeidslivet())
                    .næringsinntektBrutto(norskOrg.getNaeringsinntektBrutto().longValue())
                    .nærRelasjon(norskOrg.isNaerRelasjon())
                    .orgName(norskOrg.getOrganisasjonsnummer())
                    .orgNummer(norskOrg.getOrganisasjonsnummer())
                    .periode(tilÅpenPeriode(norskOrg.getPeriode()))
                    .regnskapsførere(tilRegnskapsFørere(norskOrg.getRegnskapsfoerer()))
                    .virksomhetsTyper(tilVirksomhetsTyper(norskOrg.getVirksomhetstype()))
                    .build();
        }
        if (egenNæring instanceof UtenlandskOrganisasjon) {
            UtenlandskOrganisasjon utenlandskOrg = UtenlandskOrganisasjon.class.cast(egenNæring);
            return no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskOrganisasjon.builder()
                    .arbeidsland(CountryCode.getByCode(utenlandskOrg.getArbeidsland().getKode()))
                    .beskrivelseEndring(utenlandskOrg.getBeskrivelseAvEndring())
                    .endringsDato(utenlandskOrg.getEndringsDato())
                    .erNyOpprettet(utenlandskOrg.isErNyoppstartet())
                    .erVarigEndring(utenlandskOrg.isErVarigEndring())
                    .erNyIArbeidslivet(utenlandskOrg.isErNyIArbeidslivet())
                    .næringsinntektBrutto(utenlandskOrg.getNaeringsinntektBrutto().longValue())
                    .nærRelasjon(utenlandskOrg.isNaerRelasjon())
                    .periode(tilÅpenPeriode(utenlandskOrg.getPeriode()))
                    .regnskapsførere(tilRegnskapsFørere(utenlandskOrg.getRegnskapsfoerer()))
                    .virksomhetsTyper(tilVirksomhetsTyper(utenlandskOrg.getVirksomhetstype()))
                    .build();
        }
        throw new IllegalArgumentException("Ikke"
                + " støttet arbeidsforhold " + egenNæring.getClass().getSimpleName());
    }

    private static CountryCode tilLand(Land land) {
        return tilLand(land, null);
    }

    private static CountryCode tilLand(Land land, CountryCode defaultLand) {
        return land == null ? defaultLand : CountryCode.getByCode(land.getKode());
    }

    private static List<Virksomhetstype> tilVirksomhetsTyper(List<Virksomhetstyper> virksomhetstype) {
        return virksomhetstype.stream().map(XMLTilSøknadMapper::tilVirksomhetsType).collect(toList());
    }

    private static Virksomhetstype tilVirksomhetsType(Virksomhetstyper type) {
        if (type == null || type.getKode().equals(UKJENT_KODEVERKSVERDI)) {
            return null;
        }
        return Virksomhetstype.valueOf(type.getKode());
    }

    private static List<Regnskapsfører> tilRegnskapsFørere(Regnskapsfoerer regnskapsfoerer) {
        if (regnskapsfoerer == null) {
            return emptyList();
        }
        return singletonList(new Regnskapsfører(regnskapsfoerer.getNavn(), regnskapsfoerer.getTelefon()));
    }

    private static List<UtenlandskArbeidsforhold> tilUtenlandsArbeidsforhold(
            List<no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v1.UtenlandskArbeidsforhold> utenlandskArbeidsforhold) {
        // TODO
        return emptyList();
    }

    private static Medlemsskap tilMedlemsskap(Medlemskap medlemskap) {
        LOG.debug("Genererer medlemsskap  modell fra {}", medlemskap);
        return new Medlemsskap(tilTidligereOpphold(medlemskap), tilFremtidigOpphold(medlemskap));
    }

    private static TidligereOppholdsInformasjon tilTidligereOpphold(Medlemskap medlemskap) {
        return new TidligereOppholdsInformasjon(true, ArbeidsInformasjon.ARBEIDET_I_NORGE, emptyList()); // TODO
    }

    private static FramtidigOppholdsInformasjon tilFremtidigOpphold(Medlemskap medlemskap) {
        return new FramtidigOppholdsInformasjon(true, true, emptyList()); // TODO
    }

    private static no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fordeling tilFordeling(Fordeling fordeling) {
        LOG.debug("Genererer fordeling  modell fra {}", fordeling);
        if (fordeling == null) {
            return null;
        }
        return new no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fordeling(fordeling.isAnnenForelderErInformert(),
                tilÅrsak(fordeling.getOenskerKvoteOverfoert()), tilPerioder(fordeling.getPerioder()));
    }

    private static Overføringsårsak tilÅrsak(Overfoeringsaarsaker årsak) {
        if (årsak == null || årsak.getKode().equals(UKJENT_KODEVERKSVERDI)) {
            return null;
        }
        return Overføringsårsak.valueOf(årsak.getKode());
    }

    private static List<LukketPeriodeMedVedlegg> tilPerioder(
            List<no.nav.vedtak.felles.xml.soeknad.uttak.v1.LukketPeriodeMedVedlegg> perioder) {
        return safeStream(perioder).map(XMLTilSøknadMapper::tilLukketPeriode).collect(toList());
    }

    private static LukketPeriodeMedVedlegg tilLukketPeriode(
            no.nav.vedtak.felles.xml.soeknad.uttak.v1.LukketPeriodeMedVedlegg periode) {

        if (periode == null) {
            return null;
        }

        if (periode instanceof Overfoeringsperiode) {
            Overfoeringsperiode overføringsPeriode = Overfoeringsperiode.class.cast(periode);
            return new OverføringsPeriode(overføringsPeriode.getFom(), overføringsPeriode.getTom(),
                    emptyList(),
                    tilÅrsak(overføringsPeriode.getAarsak()));
        }
        if (periode instanceof Oppholdsperiode) {
            Oppholdsperiode oppholdsPeriode = Oppholdsperiode.class.cast(periode);
            return new OppholdsPeriode(oppholdsPeriode.getFom(), oppholdsPeriode.getTom(), emptyList(),
                    tilÅrsak(oppholdsPeriode.getAarsak()));
        }
        if (periode instanceof Utsettelsesperiode) {
            Utsettelsesperiode utsettelse = Utsettelsesperiode.class.cast(periode);
            return new UtsettelsesPeriode(utsettelse.getFom(), utsettelse.getTom(), emptyList(),
                    tilÅrsak(utsettelse.getAarsak()));
        }

        if (periode instanceof Gradering) {
            Gradering gradering = Gradering.class.cast(periode);
            return new GradertUttaksPeriode(gradering.getFom(), gradering.getTom(), emptyList(),
                    tilStønadKontoType(gradering.getType()),
                    gradering.isOenskerSamtidigUttak(),
                    tilMorsAktivitet(gradering.getMorsAktivitetIPerioden()));
        }

        if (periode instanceof Uttaksperiode) {
            Uttaksperiode uttaksperiode = Uttaksperiode.class.cast(periode);
            return new UttaksPeriode(uttaksperiode.getFom(), uttaksperiode.getTom(), emptyList(),
                    tilStønadKontoType(uttaksperiode.getType()), uttaksperiode.isOenskerSamtidigUttak(),
                    tilMorsAktivitet(uttaksperiode.getMorsAktivitetIPerioden()));
        }

        throw new IllegalArgumentException();

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
        LOG.debug("Genererer dekningsgrad modell");

        return no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad
                .fraKode(dekningsgrad.getDekningsgrad().getKode());
    }

    private no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder tilAnnenForelder(
            AnnenForelder annenForelder) {
        if (annenForelder == null) {
            return null;
        }
        LOG.debug("Genererer annen forelder modell");

        if (annenForelder instanceof UkjentForelder) {
            return new no.nav.foreldrepenger.mottak.domain.foreldrepenger.UkjentForelder();
        }
        if (annenForelder instanceof AnnenForelderMedNorskIdent) {
            AnnenForelderMedNorskIdent norskForelder = AnnenForelderMedNorskIdent.class.cast(annenForelder);
            return new NorskForelder(oppslag.getFnr(new AktorId(norskForelder.getAktoerId())), null);
        }
        if (annenForelder instanceof AnnenForelderUtenNorskIdent) {
            AnnenForelderUtenNorskIdent utenlandsForelder = AnnenForelderUtenNorskIdent.class.cast(annenForelder);
            return new UtenlandskForelder(utenlandsForelder.getUtenlandskPersonidentifikator(),
                    tilLand(utenlandsForelder.getLand()), null);
        }
        throw new IllegalArgumentException();
    }

    private static Søker tilSøker(Bruker søker) {
        LOG.debug("Genererer søker model");

        return new Søker(BrukerRolle.valueOf(søker.getSoeknadsrolle().getKode()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + "]";
    }
}
