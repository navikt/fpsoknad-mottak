package no.nav.foreldrepenger.mottak.innsending.mappers;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper.SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.medlemsskapFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.opptjeningFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.språkFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.søkerFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.vedleggFra;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.jaxb.SVPV1JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Vedlegg;
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

    private final SVPV1JAXBUtil jaxb;

    private static final no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.ObjectFactory SVP_FACTORY_V1 = new no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.ObjectFactory();
    private static final no.nav.vedtak.felles.xml.soeknad.v3.ObjectFactory SØKNAD_FACTORY_V3 = new no.nav.vedtak.felles.xml.soeknad.v3.ObjectFactory();

    @JsonCreator
    public V1SvangerskapspengerDomainMapper() {
        this(false);
    }

    public V1SvangerskapspengerDomainMapper(boolean validate) {
        jaxb = new SVPV1JAXBUtil(validate);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return SVANGERSKAPSPENGER;
    }

    @Override
    public String tilXML(Søknad søknad, AktørId søker, SøknadEgenskap egenskap) {
        return jaxb.marshal(SØKNAD_FACTORY_V3.createSoeknad(tilModell(søknad, søker)));
    }

    @Override
    public String tilXML(Endringssøknad endringssøknad, AktørId søker, SøknadEgenskap egenskap) {
        throw new UnexpectedInputException("Endringssøknad ikke støttet for svangerskapspenger");
    }

    public Soeknad tilModell(Søknad søknad, AktørId søker) {
        return new Soeknad()
                .withSprakvalg(språkFra(søknad.getSøker()))
                .withAndreVedlegg(vedleggFra(søknad.getFrivilligeVedlegg()))
                .withPaakrevdeVedlegg(vedleggFra(søknad.getPåkrevdeVedlegg()))
                .withSoeker(søkerFra(søker, søknad.getSøker()))
                .withOmYtelse(ytelseFra(søknad))
                .withMottattDato(søknad.getMottattdato())
                .withBegrunnelseForSenSoeknad(søknad.getBegrunnelseForSenSøknad())
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger());
    }

    private OmYtelse ytelseFra(Søknad søknad) {
        no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger ytelse = no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger.class
                .cast(søknad.getYtelse());
        return new OmYtelse().withAny(jaxb.marshalToElement(svangerskapspengerFra(ytelse)));
    }

    private static JAXBElement<Svangerskapspenger> svangerskapspengerFra(
            no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger ytelse) {
        return SVP_FACTORY_V1.createSvangerskapspenger(new Svangerskapspenger()
                .withTermindato(ytelse.getTermindato())
                .withFødselsdato(ytelse.getFødselsdato())
                .withOpptjening(opptjeningFra(ytelse.getOpptjening()))
                .withTilretteleggingListe(tilretteleggingFra(ytelse.getTilrettelegging()))
                .withMedlemskap(medlemsskapFra(ytelse.getMedlemsskap(),
                        relasjonsDatoFra(ytelse.getTermindato(), ytelse.getFødselsdato()))));
    }


    private static TilretteleggingListe tilretteleggingFra(
        List<no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging> tilrettelegginger) {

        List<Tilrettelegging> tilRetteleggingsListe = tilretteleggingByArbeidsforhold(tilrettelegginger)
            .entrySet().stream()
            .map(e -> create(e.getValue()))
            .collect(toList());

        return new TilretteleggingListe()
            .withTilrettelegging(tilRetteleggingsListe);
    }


    private static Tilrettelegging create(
            List<no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging> tiltakListe) {
        Tilrettelegging tilrettelegging = new Tilrettelegging();

        for (no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging tiltak : tiltakListe) {
            if (tiltak instanceof no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging) {
                tilrettelegging.withIngenTilrettelegging(ingenTilretteleggingFra(tiltak));
            } else if (tiltak instanceof no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging) {
                tilrettelegging.withDelvisTilrettelegging(delvisTilretteleggingFra(tiltak));
            } else if (tiltak instanceof no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging) {
                tilrettelegging.withHelTilrettelegging(helTilretteleggingFra(tiltak));
            } else {
                throw new UnexpectedInputException("Ukjent tilrettelegging %s", tilrettelegging.getClass().getSimpleName());
            }
        }

        tiltakListe.stream()
            .findAny()
            .ifPresent(b -> {
                tilrettelegging.withBehovForTilretteleggingFom(b.getBehovForTilretteleggingFom());
                tilrettelegging.withVedlegg(tilretteleggingVedleggFraIDs(b.getVedlegg()));
                tilrettelegging.withArbeidsforhold(arbeidsforholdFra(b.getArbeidsforhold()));
            });

        return tilrettelegging;
    }

    private static IngenTilrettelegging ingenTilretteleggingFra(no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging ingen) {
        return new IngenTilrettelegging().withSlutteArbeidFom(no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging.class.cast(ingen).getSlutteArbeidFom());
    }

    private static DelvisTilrettelegging delvisTilretteleggingFra(no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging delvis) {
        no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging delvisTilrettelegging = no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging.class.cast(delvis);
        return new DelvisTilrettelegging()
            .withTilrettelagtArbeidFom(delvisTilrettelegging.getTilrettelagtArbeidFom())
            .withStillingsprosent(BigDecimal.valueOf(prosentFra(delvisTilrettelegging.getStillingsprosent())));
    }

    private static HelTilrettelegging helTilretteleggingFra(no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging hel) {
        no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging helTilrettelegging = no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging.class.cast(hel);
        return new HelTilrettelegging().withTilrettelagtArbeidFom(helTilrettelegging.getTilrettelagtArbeidFom());
    }

    private static double prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::getProsent)
                .orElse(0d);
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

        throw new UnexpectedInputException("Ukjent arbeidsforhold %s", forhold.getClass().getSimpleName());
    }

    private static LocalDate relasjonsDatoFra(LocalDate termindato, LocalDate fødselsdato) {
        return Optional.ofNullable(fødselsdato)
                .orElse(termindato);
    }

    private static Map<no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold, List<no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging>> tilretteleggingByArbeidsforhold(
        List<no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging> tilretteleggingsPerioder) {
        Map<no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold, List<no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging>> tilretteleggingByArbeidsforhold = new HashMap<>();
        tilretteleggingsPerioder.forEach(tp -> tilretteleggingByArbeidsforhold
            .computeIfAbsent(tp.getArbeidsforhold(), key -> new ArrayList<>())
            .add(tp));
        return tilretteleggingByArbeidsforhold;
    }

    private static List<no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging> sortertTilretteleggingsliste(List<no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging> liste) {
        return safeStream(liste)
            .sorted(Comparator.comparing(no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging::getBehovForTilretteleggingFom))
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapperEgenskaper=" + mapperEgenskaper() + "]";
    }

}
