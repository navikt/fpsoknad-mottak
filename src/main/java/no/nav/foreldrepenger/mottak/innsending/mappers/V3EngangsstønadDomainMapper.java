package no.nav.foreldrepenger.mottak.innsending.mappers;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.landFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.medlemsskapFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.søkerFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.vedleggFra;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V3;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.jaxb.ESV3JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelder;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderMedNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.AnnenForelderUtenNorskIdent;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Foedsel;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.SoekersRelasjonTilBarnet;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Termin;
import no.nav.vedtak.felles.xml.soeknad.v3.OmYtelse;
import no.nav.vedtak.felles.xml.soeknad.v3.Soeknad;

@Component
public class V3EngangsstønadDomainMapper implements DomainMapper {
    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V3, INITIELL_ENGANGSSTØNAD);

    private static final ESV3JAXBUtil JAXB = new ESV3JAXBUtil();

    private static final no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v3.ObjectFactory ES_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v3.ObjectFactory();
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
    public String tilXML(Søknad søknad, AktørId søker, SøknadEgenskap egenskap) {
        return JAXB.marshal(SØKNAD_FACTORY_V3.createSoeknad(tilModell(søknad, søker)));
    }

    @Override
    public String tilXML(Endringssøknad endringssøknad, AktørId søker, SøknadEgenskap egenskap) {
        throw new UnsupportedOperationException("Endringssøknad ikke støttet");
    }

    private Soeknad tilModell(Søknad søknad, AktørId søker) {
        return new Soeknad()
                .withAndreVedlegg(vedleggFra(søknad.getFrivilligeVedlegg()))
                .withPaakrevdeVedlegg(vedleggFra(søknad.getPåkrevdeVedlegg()))
                .withSoeker(søkerFra(søker, søknad.getSøker()))
                .withMottattDato(søknad.getMottattdato())
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger())
                .withOmYtelse(engangsstønadFra(søknad));
    }

    private OmYtelse engangsstønadFra(Søknad søknad) {
        var ytelse = Engangsstønad.class.cast(søknad.getYtelse());
        return new OmYtelse().withAny(JAXB.marshalToElement(engangsstønadFra(ytelse, søknad.getVedlegg())));
    }

    private JAXBElement<no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v3.Engangsstønad> engangsstønadFra(
            no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad es, List<Vedlegg> vedlegg) {
        return ES_FACTORY_V3.createEngangsstønad(new no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v3.Engangsstønad()
                .withMedlemskap(medlemsskapFra(es.getMedlemsskap(), es.getRelasjonTilBarn().relasjonsDato()))
                .withSoekersRelasjonTilBarnet(relasjonFra(es.getRelasjonTilBarn(), vedlegg))
                .withAnnenForelder(annenForelderFra(es.getAnnenForelder())));
    }

    private static SoekersRelasjonTilBarnet relasjonFra(RelasjonTilBarn relasjon, List<Vedlegg> vedlegg) {
        if (relasjon instanceof FremtidigFødsel) {
            return create(FremtidigFødsel.class.cast(relasjon), vedlegg);
        }
        if (relasjon instanceof Fødsel) {
            return create(Fødsel.class.cast(relasjon), vedlegg);
        }
        throw new UnexpectedInputException("Relasjon %s er ikke støttet", relasjon.getClass().getSimpleName());
    }

    private static SoekersRelasjonTilBarnet create(Fødsel fødsel, List<Vedlegg> vedlegg) {
        return new Foedsel()
                .withVedlegg(relasjonTilBarnVedleggFra(vedlegg))
                .withFoedselsdato(fødsel.getFødselsdato().get(0))
                .withAntallBarn(fødsel.getAntallBarn());
    }

    private static SoekersRelasjonTilBarnet create(FremtidigFødsel termin, List<Vedlegg> vedlegg) {
        return new Termin()
                .withVedlegg(relasjonTilBarnVedleggFra(vedlegg))
                .withTermindato(termin.getTerminDato())
                .withUtstedtdato(termin.getUtstedtDato())
                .withAntallBarn(termin.getAntallBarn());
    }

    private static List<JAXBElement<Object>> relasjonTilBarnVedleggFra(List<Vedlegg> vedlegg) {
        return safeStream(vedlegg)
                .map(Vedlegg::getId)
                .map(s -> FELLES_FACTORY_V3.createSoekersRelasjonTilBarnetVedlegg(
                        new no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg().withId(s)))
                .collect(toList());
    }

    private AnnenForelder annenForelderFra(
            no.nav.foreldrepenger.mottak.domain.felles.annenforelder.AnnenForelder annenForelder) {
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
        throw new UnexpectedInputException("Ukjent annen forelder %s", annenForelder.getClass().getSimpleName());
    }

    private static AnnenForelder utenlandskForelderFra(UtenlandskForelder utenlandskFar) {
        return new AnnenForelderUtenNorskIdent()
                .withUtenlandskPersonidentifikator(utenlandskFar.getId())
                .withLand(landFra(utenlandskFar.getLand()));
    }

    private AnnenForelder norskForelderFra(NorskForelder norskForelder) {
        if (norskForelder.hasId()) {
            return new AnnenForelderMedNorskIdent().withAktoerId(oppslag.getAktørId(norskForelder.getFnr()).getId());
        }
        return null;
    }

    private static AnnenForelder ukjentForelder() {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v3.UkjentForelder();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", mapperEgenskaper=" + mapperEgenskaper() + "]";
    }
}
