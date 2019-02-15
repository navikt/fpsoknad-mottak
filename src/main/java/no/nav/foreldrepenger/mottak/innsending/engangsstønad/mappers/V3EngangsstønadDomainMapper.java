package no.nav.foreldrepenger.mottak.innsending.engangsstønad.mappers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.SEND_SENERE;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V3;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.UkjentForelder;
import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.Fødsel;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.domain.felles.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.DomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.jaxb.ESV3JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v3.ObjectFactory;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderMedNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderUtenNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Foedsel;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.OppholdNorge;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.OppholdUtlandet;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Periode;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.SoekersRelasjonTilBarnet;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Termin;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Brukerroller;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Innsendingstype;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Land;
import no.nav.vedtak.felles.xml.soeknad.v3.OmYtelse;
import no.nav.vedtak.felles.xml.soeknad.v3.Soeknad;

@Component
public class V3EngangsstønadDomainMapper implements DomainMapper {
    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V3, INITIELL_ENGANGSSTØNAD);

    private static final ESV3JAXBUtil JAXB = new ESV3JAXBUtil();
    private static final Logger LOG = LoggerFactory.getLogger(V3EngangsstønadDomainMapper.class);

    private static final ObjectFactory ES_FACTORY_V3 = new ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.v3.ObjectFactory SØKNAD_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.v3.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.felles.v3.ObjectFactory FELLES_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.felles.v3.ObjectFactory();

    private final Oppslag oppslag;

    public V3EngangsstønadDomainMapper(Oppslag oppslag) {
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
        throw new UnsupportedOperationException();
    }

    private Soeknad tilModell(Søknad søknad, AktorId søker) {
        no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad es = no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad.class
                .cast(søknad.getYtelse());
        LOG.debug(CONFIDENTIAL, "Genererer søknad XML fra {}", es);
        return new Soeknad()
                .withAndreVedlegg(vedleggFra(søknad.getFrivilligeVedlegg()))
                .withPaakrevdeVedlegg(vedleggFra(søknad.getPåkrevdeVedlegg()))
                .withSoeker(søkerFra(søker, søknad.getSøker()))
                .withMottattDato(søknad.getMottattdato().toLocalDate())
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger())
                .withOmYtelse(engangsstønadFra(søknad));
    }

    private OmYtelse engangsstønadFra(Søknad søknad) {
        Engangsstønad ytelse = Engangsstønad.class.cast(søknad.getYtelse());
        LOG.debug(CONFIDENTIAL, "Genererer ytelse XML fra {}", ytelse);
        return new OmYtelse().withAny(JAXB.marshalToElement(engangsstønadFra(ytelse, søknad.getVedlegg())));
    }

    private JAXBElement<no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v3.Engangsstønad> engangsstønadFra(
            no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad es, List<Vedlegg> vedlegg) {
        return ES_FACTORY_V3.createEngangsstønad(new no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v3.Engangsstønad()
                .withMedlemskap(medlemsskapFra(es.getMedlemsskap(), es.getRelasjonTilBarn()))
                .withSoekersRelasjonTilBarnet(relasjonFra(es.getRelasjonTilBarn(), vedlegg))
                .withAnnenForelder(annenForelderFra(es.getAnnenForelder())));
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

    private static SoekersRelasjonTilBarnet relasjonFra(RelasjonTilBarn relasjon, List<Vedlegg> vedlegg) {
        if (relasjon instanceof FremtidigFødsel) {
            return terminFra(FremtidigFødsel.class.cast(relasjon), vedlegg);
        }
        if (relasjon instanceof Fødsel) {
            return fødselFra(Fødsel.class.cast(relasjon), vedlegg);
        }
        throw new IllegalArgumentException(
                "Relasjon til barn " + relasjon.getClass().getSimpleName() + " er foreløpig ikke støttet");
    }

    private static SoekersRelasjonTilBarnet fødselFra(Fødsel fødsel, List<Vedlegg> vedlegg) {
        return new Foedsel()
                .withVedlegg(relasjonTilBarnVedleggFra(vedlegg))
                .withFoedselsdato(fødsel.getFødselsdato().get(0))
                .withAntallBarn(fødsel.getAntallBarn());
    }

    private static SoekersRelasjonTilBarnet terminFra(FremtidigFødsel termin, List<Vedlegg> vedlegg) {
        return new Termin()
                .withVedlegg(relasjonTilBarnVedleggFra(vedlegg))
                .withTermindato(termin.getTerminDato())
                .withUtstedtdato(termin.getUtstedtDato())
                .withAntallBarn(termin.getAntallBarn());
    }

    private static List<JAXBElement<Object>> relasjonTilBarnVedleggFra(List<Vedlegg> vedlegg) {
        return vedlegg.stream()
                .map(s -> s.getId())
                .map(s -> FELLES_FACTORY_V3.createSoekersRelasjonTilBarnetVedlegg(
                        new no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg().withId(s)))
                .collect(toList());
    }

    private AnnenForelder annenForelderFra(
            no.nav.foreldrepenger.mottak.domain.felles.AnnenForelder annenForelder) {
        if (annenForelder == null) {
            return null;
        }
        if (annenForelder instanceof UkjentForelder) {
            return ukjentForelder();
        }
        if (annenForelder instanceof NorskForelder) {
            return norskForelderFra(NorskForelder.class.cast(annenForelder));
        }
        if (annenForelder instanceof UtenlandskForelder) {
            return utenlandskForelderFra(UtenlandskForelder.class.cast(annenForelder));
        }
        throw new IllegalArgumentException("Dette skal aldri skje, hva har du gjort nå, da ?");
    }

    private static AnnenForelder utenlandskForelderFra(UtenlandskForelder utenlandskFar) {
        return new AnnenForelderUtenNorskIdent()
                .withUtenlandskPersonidentifikator(utenlandskFar.getId())
                .withLand(landFra(utenlandskFar.getLand()));
    }

    private AnnenForelder norskForelderFra(NorskForelder norskForelder) {
        if (norskForelder.hasId()) {
            return new AnnenForelderMedNorskIdent()
                    .withAktoerId(oppslag.getAktørId(norskForelder.getFnr()).getId());
        }
        return null;
    }

    private static AnnenForelder ukjentForelder() {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v3.UkjentForelder();
    }

    private static Medlemskap medlemsskapFra(Medlemsskap medlemsskap, RelasjonTilBarn relasjon) {
        Medlemskap ms = new Medlemskap()
                .withOppholdUtlandet(oppholdUtlandetFra(medlemsskap.getTidligereOppholdsInfo(),
                        medlemsskap.getFramtidigOppholdsInfo()))
                .withINorgeVedFoedselstidspunkt(
                        medlemsskap.landVedDato(relasjon.relasjonsDato()).equals(CountryCode.NO))
                .withBorINorgeNeste12Mnd(oppholdINorgeNeste12(medlemsskap))
                .withBoddINorgeSiste12Mnd(oppholdINorgeSiste12(medlemsskap));
        if (kunOppholdINorgeSisteOgNeste12(medlemsskap)) {
            ms.withOppholdNorge(kunOppholdINorgeSisteOgNeste12());
        }
        return ms;
    }

    private static boolean kunOppholdINorgeSisteOgNeste12(Medlemsskap ms) {
        return oppholdINorgeSiste12(ms) && oppholdINorgeNeste12(ms);
    }

    private static boolean oppholdINorgeSiste12(Medlemsskap ms) {
        return ms.getTidligereOppholdsInfo().isBoddINorge();
    }

    private static boolean oppholdINorgeNeste12(Medlemsskap ms) {
        return ms.getFramtidigOppholdsInfo().isNorgeNeste12();
    }

    private static List<OppholdNorge> kunOppholdINorgeSisteOgNeste12() {
        return Lists.newArrayList(new OppholdNorge()
                .withPeriode(new Periode()
                        .withFom(LocalDate.now().minusYears(1))
                        .withTom(LocalDate.now())),
                new OppholdNorge().withPeriode(new Periode()
                        .withFom(LocalDate.now())
                        .withTom(LocalDate.now().plusYears(1))));
    }

    private static List<OppholdUtlandet> oppholdUtlandetFra(TidligereOppholdsInformasjon tidligereOppholdsInfo,
            FramtidigOppholdsInformasjon framtidigOppholdsInfo) {
        if (tidligereOppholdsInfo.isBoddINorge() && framtidigOppholdsInfo.isNorgeNeste12()) {
            return emptyList();
        }
        return Stream
                .concat(safeStream(tidligereOppholdsInfo.getUtenlandsOpphold()),
                        safeStream(framtidigOppholdsInfo.getUtenlandsOpphold()))
                .map(V3EngangsstønadDomainMapper::utenlandOppholdFra)
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
        return Optional.ofNullable(land)
                .map(s -> landFra(s.getAlpha3()))
                .orElse(null);
    }

    private static Land landFra(String alphq3) {
        Land land = new Land().withKode(alphq3);
        return land.withKodeverk(land.getKodeverk());
    }

    private static List<no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg> vedleggFra(
            List<? extends no.nav.foreldrepenger.mottak.domain.felles.Vedlegg> vedlegg) {
        return safeStream(vedlegg)
                .map(V3EngangsstønadDomainMapper::vedleggFra)
                .collect(toList());
    }

    private static no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg vedleggFra(
            no.nav.foreldrepenger.mottak.domain.felles.Vedlegg vedlegg) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg()
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", mapperEgenskaper=" + mapperEgenskaper() + "]";
    }
}
