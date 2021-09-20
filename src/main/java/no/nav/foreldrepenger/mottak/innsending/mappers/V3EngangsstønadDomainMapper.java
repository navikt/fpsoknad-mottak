package no.nav.foreldrepenger.mottak.innsending.mappers;

import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.util.Versjon.V3;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.landFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.medlemsskapFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.målformFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.søkerFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.vedleggFra;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;
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
                .withSprakvalg(målformFra(søknad.getSøker()))
                .withAndreVedlegg(vedleggFra(søknad.getFrivilligeVedlegg()))
                .withPaakrevdeVedlegg(vedleggFra(søknad.getPåkrevdeVedlegg()))
                .withSoeker(søkerFra(søker, søknad.getSøker()))
                .withMottattDato(søknad.getMottattdato())
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger())
                .withOmYtelse(engangsstønadFra(søknad));
    }

    private OmYtelse engangsstønadFra(Søknad søknad) {
        return new OmYtelse().withAny(JAXB
                .marshalToElement(engangsstønadFra(Engangsstønad.class.cast(søknad.getYtelse()), søknad.getVedlegg())));
    }

    private JAXBElement<no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v3.Engangsstønad> engangsstønadFra(
            no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad es, List<Vedlegg> vedlegg) {
        return ES_FACTORY_V3.createEngangsstønad(new no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v3.Engangsstønad()
                .withMedlemskap(medlemsskapFra(es.getMedlemsskap(), es.getRelasjonTilBarn().relasjonsDato()))
                .withSoekersRelasjonTilBarnet(relasjonFra(es.getRelasjonTilBarn(), vedlegg))
                .withAnnenForelder(annenForelderFra(es.getAnnenForelder())));
    }

    private static SoekersRelasjonTilBarnet relasjonFra(RelasjonTilBarn relasjon, List<Vedlegg> vedlegg) {
        if (relasjon instanceof FremtidigFødsel f) {
            return create(f, vedlegg);
        }
        if (relasjon instanceof Fødsel f) {
            return create(f, vedlegg);
        }
        if (relasjon instanceof Adopsjon a) {
            return create(a, vedlegg);
        }
        throw new UnexpectedInputException("Relasjon %s er ikke støttet", relasjon.getClass().getSimpleName());
    }

    private static SoekersRelasjonTilBarnet create(Adopsjon adopsjon, List<Vedlegg> vedlegg) {
        return new no.nav.vedtak.felles.xml.soeknad.felles.v3.Adopsjon()
                .withVedlegg(relasjonTilBarnVedleggFra(vedlegg))
                .withAntallBarn(adopsjon.getAntallBarn())
                .withFoedselsdato(adopsjon.getFødselsdato())
                .withOmsorgsovertakelsesdato(adopsjon.getOmsorgsovertakelsesdato())
                .withAdopsjonAvEktefellesBarn(adopsjon.isEktefellesBarn())
                .withAnkomstdato(adopsjon.getAnkomstDato());
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
                .toList();
    }

    private AnnenForelder annenForelderFra(
            no.nav.foreldrepenger.common.domain.felles.annenforelder.AnnenForelder annenForelder) {
        if (annenForelder == null) {
            return null;
        }
        if (annenForelder instanceof UkjentForelder) {
            return ukjentForelder();
        }
        if (annenForelder instanceof NorskForelder n) {
            return norskForelderFra(n);
        }
        if (annenForelder instanceof UtenlandskForelder u) {
            return utenlandskForelderFra(u);
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
            return new AnnenForelderMedNorskIdent().withAktoerId(oppslag.aktørId(norskForelder.getFnr()).getId());
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
