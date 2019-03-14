package no.nav.foreldrepenger.mottak.innsending.mappers;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.andreOpptjeningerFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.egneNæringerFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.frilansFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.landFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.språkFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.søkerFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.utenlandskeArbeidsforholdFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperUtils.vedleggFra;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.jaxb.SVPV1JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Medlemskap;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.OppholdUtlandet;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Periode;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Opptjening;
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

    private static Opptjening opptjeningFra(
            no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening opptjening) {
        return new Opptjening()
                .withUtenlandskArbeidsforhold(utenlandskeArbeidsforholdFra(opptjening.getUtenlandskArbeidsforhold()))
                .withFrilans(frilansFra(opptjening.getFrilans()))
                .withEgenNaering(egneNæringerFra(opptjening.getEgenNæring()))
                .withAnnenOpptjening(andreOpptjeningerFra(opptjening.getAnnenOpptjening()));
    }

    private static LocalDate relasjonsDatoFra(LocalDate termindato, LocalDate fødselsdato) {
        return Optional.ofNullable(fødselsdato)
                .orElse(termindato);
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapperEgenskaper=" + mapperEgenskaper() + "]";
    }

}
