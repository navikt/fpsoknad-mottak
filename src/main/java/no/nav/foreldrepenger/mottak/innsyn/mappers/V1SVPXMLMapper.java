package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.jaxb.SVPV1JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Bruker;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.OppholdUtlandet;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Land;
import no.nav.vedtak.felles.xml.soeknad.v3.OmYtelse;
import no.nav.vedtak.felles.xml.soeknad.v3.Soeknad;

@Component
public class V1SVPXMLMapper implements XMLSøknadMapper {

    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V1, INITIELL_SVANGERSKAPSPENGER);

    private static final SVPV1JAXBUtil JAXB = new SVPV1JAXBUtil();

    private static final Logger LOG = LoggerFactory.getLogger(V1SVPXMLMapper.class);

    private final Oppslag oppslag;

    public V1SVPXMLMapper(Oppslag oppslag) {
        this.oppslag = oppslag;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return EGENSKAPER;
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        return Optional.ofNullable(xml)
                .map(V1SVPXMLMapper::svpSøknad)
                .orElse(null);
    }

    private static Søknad svpSøknad(String xml) {
        try {
            Soeknad søknad = JAXB.unmarshalToElement(xml, Soeknad.class).getValue();
            Søknad s = new Søknad(søknad.getMottattDato().atStartOfDay(), tilSøker(søknad.getSoeker()),
                    tilYtelse(søknad.getOmYtelse(), søknad.getMottattDato()));
            s.setBegrunnelseForSenSøknad(søknad.getBegrunnelseForSenSoeknad());
            s.setTilleggsopplysninger(søknad.getTilleggsopplysninger());
            return s;
        } catch (Exception e) {
            LOG.debug("Feil ved unmarshalling av svangerskapspengesøknad, ikke kritisk, vi bruker ikke dette til noe",
                    e);
            return null;
        }
    }

    private static Søker tilSøker(Bruker søker) {
        return new Søker(tilRolle(søker.getSoeknadsrolle().getKode()));
    }

    private static Svangerskapspenger tilYtelse(OmYtelse omYtelse, LocalDate søknadsDato) {
        no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Svangerskapspenger søknad = ytelse(omYtelse);
        return new Svangerskapspenger(søknad.getTermindato(), søknad.getFødselsdato(),
                tilMedlemsskap(søknad.getMedlemskap(), søknadsDato), null, null);
    }

    private static Medlemsskap tilMedlemsskap(Medlemskap medlemskap, LocalDate søknadsDato) {
        TidligereOppholdsInformasjon tidligere = new TidligereOppholdsInformasjon(ArbeidsInformasjon.IKKE_ARBEIDET,
                utenlandsOppholdFør(medlemskap.getOppholdUtlandet(), søknadsDato));
        FramtidigOppholdsInformasjon framtidig = new FramtidigOppholdsInformasjon(
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
                .collect(toList());
    }

    private static Predicate<? super OppholdUtlandet> før(LocalDate søknadsDato) {
        return f -> f.getPeriode().getFom().isBefore(søknadsDato);
    }

    private static Predicate<? super OppholdUtlandet> etter(LocalDate søknadsDato) {
        return f -> f.getPeriode().getFom().isAfter(søknadsDato);
    }

    private static no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Svangerskapspenger ytelse(OmYtelse omYtelse) {
        if (omYtelse == null || omYtelse.getAny() == null || omYtelse.getAny().isEmpty()) {
            LOG.warn("Ingen ytelse i søknaden");
            return null;
        }
        if (omYtelse.getAny().size() > 1) {
            LOG.warn("Fikk {} ytelser i søknaden, forventet 1, behandler kun den første", omYtelse.getAny().size());
        }
        return (no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Svangerskapspenger) ((JAXBElement<?>) omYtelse
                .getAny().get(0)).getValue();
    }

    private static BrukerRolle tilRolle(String kode) {
        return Optional.of(kode)
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
