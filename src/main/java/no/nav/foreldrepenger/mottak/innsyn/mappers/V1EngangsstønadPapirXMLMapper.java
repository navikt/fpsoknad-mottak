package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.oppslag.dkif.Målform;
import no.nav.foreldrepenger.mottak.util.jaxb.ESV1JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Adopsjon;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelderMedNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelderUtenNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Foedsel;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.OppholdUtlandet;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.SoekersRelasjonTilBarnet;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.Termin;
import no.nav.vedtak.felles.xml.soeknad.felles.v1.UkjentForelder;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v1.Land;
import no.nav.vedtak.felles.xml.soeknad.v1.OmYtelse;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

@Component
public class V1EngangsstønadPapirXMLMapper implements XMLSøknadMapper {
    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V1, INITIELL_ENGANGSSTØNAD);
    private final ESV1JAXBUtil JAXB;
    private static final Logger LOG = LoggerFactory.getLogger(V1EngangsstønadPapirXMLMapper.class);
    private final Oppslag oppslag;

    @Inject
    public V1EngangsstønadPapirXMLMapper(Oppslag oppslag) {
        this(oppslag, false);
    }

    public V1EngangsstønadPapirXMLMapper(Oppslag oppslag, boolean validate) {
        this.oppslag = oppslag;
        this.JAXB = new ESV1JAXBUtil(validate);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return EGENSKAPER;
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        return Optional.ofNullable(xml)
                .map(this::esPapir)
                .orElse(null);
    }

    private Søknad esPapir(String xml) {
        try {
            Soeknad søknad = JAXB.unmarshalToElement(xml, Soeknad.class).getValue();
            Søknad s = new Søknad(søknad.getMottattDato(), tilSøker(søknad.getSoeker()),
                    tilYtelse(søknad.getOmYtelse(), søknad.getMottattDato()));
            s.setBegrunnelseForSenSøknad(søknad.getBegrunnelseForSenSoeknad());
            s.setTilleggsopplysninger(søknad.getTilleggsopplysninger());
            return s;
        } catch (Exception e) {
            LOG.debug("Feil ved unmarshalling av papirsøknad, ikke kritisk foreløpig, vi bruker ikke dette til noe", e);
            return null;
        }
    }

    private static Søker tilSøker(Bruker søker) {
        return new Søker(tilRolle(søker.getSoeknadsrolle().getKode()), Målform.standard());
    }

    private Engangsstønad tilYtelse(OmYtelse omYtelse, LocalDate søknadsDato) {
        no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v1.Engangsstønad søknad = ytelse(omYtelse);
        if (søknad != null) {
            var stønad = new Engangsstønad(tilMedlemsskap(søknad.getMedlemskap(), søknadsDato),
                    tilRelasjonTilBarn(søknad.getSoekersRelasjonTilBarnet()));
            stønad.setAnnenForelder(tilAnnenForelder(søknad.getAnnenForelder()));
            return stønad;
        }
        return null;
    }

    private static RelasjonTilBarn tilRelasjonTilBarn(SoekersRelasjonTilBarnet relasjonTilBarnet) {
        if (relasjonTilBarnet == null) {
            return null;
        }
        if (relasjonTilBarnet instanceof Foedsel fødsel) {
            return new Fødsel(
                    fødsel.getAntallBarn(),
                    fødsel.getFoedselsdato());
        }
        if (relasjonTilBarnet instanceof Termin termin) {
            return new FremtidigFødsel(
                    termin.getAntallBarn(),
                    termin.getTermindato(),
                    termin.getUtstedtdato(),
                    emptyList());
        }
        if (relasjonTilBarnet instanceof Adopsjon adopsjon) {
            return new no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Adopsjon(
                    adopsjon.getAntallBarn(),
                    adopsjon.getOmsorgsovertakelsesdato(),
                    adopsjon.isAdopsjonAvEktefellesBarn(),
                    false,
                    emptyList(),
                    adopsjon.getAnkomstdato(),
                    adopsjon.getFoedselsdato());
        }
        throw new UnexpectedInputException("Ikke-støttet type " + relasjonTilBarnet.getClass().getSimpleName());
    }

    private static Medlemsskap tilMedlemsskap(Medlemskap medlemskap, LocalDate søknadsDato) {
        var tidligere = new TidligereOppholdsInformasjon(ArbeidsInformasjon.IKKE_ARBEIDET,
                utenlandsOppholdFør(medlemskap.getOppholdUtlandet(), søknadsDato));
        var framtidig = new FramtidigOppholdsInformasjon(
                utenlandsOppholdEtter(medlemskap.getOppholdUtlandet(), søknadsDato));
        return new Medlemsskap(tidligere, framtidig);
    }

    private static List<Utenlandsopphold> utenlandsOppholdFør(List<OppholdUtlandet> opphold, LocalDate søknadsDato) {
        return utenlandsOpphold(opphold, søknadsDato, før(søknadsDato));
    }

    private static List<Utenlandsopphold> utenlandsOppholdEtter(List<OppholdUtlandet> opphold, LocalDate søknadsDato) {
        return utenlandsOpphold(opphold, søknadsDato, etter(søknadsDato));
    }

    private static List<Utenlandsopphold> utenlandsOpphold(List<OppholdUtlandet> opphold, LocalDate søknadsDato,
            Predicate<? super OppholdUtlandet> predicate) {
        return safeStream(opphold)
                .filter(predicate)
                .map(u -> new Utenlandsopphold(tilLand(u.getLand()),
                        new LukketPeriode(u.getPeriode().getFom(), u.getPeriode().getTom())))
                .toList();
    }

    private static Predicate<? super OppholdUtlandet> før(LocalDate søknadsDato) {
        return f -> f.getPeriode().getFom().isBefore(søknadsDato);
    }

    private static Predicate<? super OppholdUtlandet> etter(LocalDate søknadsDato) {
        return f -> f.getPeriode().getFom().isAfter(søknadsDato);
    }

    private AnnenForelder tilAnnenForelder(
            no.nav.vedtak.felles.xml.soeknad.felles.v1.AnnenForelder annenForelder) {
        if (annenForelder == null) {
            return null;
        }
        if (annenForelder instanceof UkjentForelder) {
            return new no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UkjentForelder();
        }
        if (annenForelder instanceof AnnenForelderMedNorskIdent norskForelder) {
            return new NorskForelder(
                    oppslag.fnr(new AktørId(norskForelder.getAktoerId())),
                    null);
        }
        if (annenForelder instanceof AnnenForelderUtenNorskIdent utenlandsForelder) {
            return new UtenlandskForelder(
                    utenlandsForelder.getUtenlandskPersonidentifikator(),
                    tilLand(utenlandsForelder.getLand()),
                    null);
        }
        throw new UnexpectedInputException("Ukjent annen forelder %s", annenForelder.getClass().getSimpleName());
    }

    private static no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v1.Engangsstønad ytelse(OmYtelse omYtelse) {
        if ((omYtelse == null) || (omYtelse.getAny() == null) || omYtelse.getAny().isEmpty()) {
            LOG.warn("Ingen ytelse i søknaden");
            return null;
        }
        if (omYtelse.getAny().size() > 1) {
            LOG.warn("Fikk {} ytelser i søknaden, forventet 1, behandler kun den første", omYtelse.getAny().size());
        }
        return (no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v1.Engangsstønad) omYtelse.getAny().get(0);
    }

    private static BrukerRolle tilRolle(String kode) {
        return Optional.ofNullable(kode)
                .map(BrukerRolle::valueOf)
                .orElse(BrukerRolle.IKKE_RELEVANT);
    }

    private static CountryCode tilLand(Land land) {
        return tilLand(land, null);
    }

    private static CountryCode tilLand(Land land, CountryCode defaultLand) {
        return Optional.ofNullable(land)
                .map(Land::getKode)
                .map(CountryCode::getByCode)
                .orElse(defaultLand);
    }
}
