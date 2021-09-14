package no.nav.foreldrepenger.mottak.innsending.mappers;

import static no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper.SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.medlemsskapFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.målformFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.opptjeningFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.søkerFra;
import static no.nav.foreldrepenger.mottak.innsending.mappers.V3DomainMapperCommon.vedleggFra;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;
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
                .withSprakvalg(målformFra(søknad.getSøker()))
                .withAndreVedlegg(vedleggFra(søknad.getFrivilligeVedlegg()))
                .withPaakrevdeVedlegg(vedleggFra(søknad.getPåkrevdeVedlegg()))
                .withSoeker(søkerFra(søker, søknad.getSøker()))
                .withOmYtelse(ytelseFra(søknad))
                .withMottattDato(søknad.getMottattdato())
                .withBegrunnelseForSenSoeknad(søknad.getBegrunnelseForSenSøknad())
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger());
    }

    private OmYtelse ytelseFra(Søknad søknad) {
        var ytelse = no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger.class
                .cast(søknad.getYtelse());
        return new OmYtelse()
                .withAny(jaxb.marshalToElement(svangerskapspengerFra(ytelse)));
    }

    private static JAXBElement<Svangerskapspenger> svangerskapspengerFra(
            no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger ytelse) {
        return SVP_FACTORY_V1.createSvangerskapspenger(new Svangerskapspenger()
                .withTermindato(ytelse.getTermindato())
                .withFødselsdato(ytelse.getFødselsdato())
                .withOpptjening(opptjeningFra(ytelse.getOpptjening()))
                .withTilretteleggingListe(tilretteleggingFra(ytelse.getTilrettelegging()))
                .withMedlemskap(medlemsskapFra(ytelse.getMedlemsskap(),
                        relasjonsDatoFra(ytelse.getTermindato(), ytelse.getFødselsdato()))));
    }

    private static TilretteleggingListe tilretteleggingFra(
            List<no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.Tilrettelegging> tilrettelegginger) {
        return new TilretteleggingListe()
                .withTilrettelegging(
                        tilretteleggingByArbeidsforhold(tilrettelegginger)
                                .values().stream()
                                .map(V1SvangerskapspengerDomainMapper::create)
                                .toList());
    }

    private static Tilrettelegging create(
            List<no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.Tilrettelegging> tiltakListe) {
        var tilrettelegging = new Tilrettelegging();

        for (no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.Tilrettelegging t : tiltakListe) {
            if (t instanceof no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging i) {
                tilrettelegging.withIngenTilrettelegging(ingenTilretteleggingFra(i));
            } else if (t instanceof no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging d) {
                tilrettelegging.withDelvisTilrettelegging(delvisTilretteleggingFra(d));
            } else if (t instanceof no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging h) {
                tilrettelegging.withHelTilrettelegging(helTilretteleggingFra(h));
            } else {
                throw new UnexpectedInputException("Ukjent tilrettelegging %s",
                        tilrettelegging.getClass().getSimpleName());
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

    private static IngenTilrettelegging ingenTilretteleggingFra(
            no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging ingen) {
        return new IngenTilrettelegging()
                .withSlutteArbeidFom(ingen.getSlutteArbeidFom());
    }

    private static DelvisTilrettelegging delvisTilretteleggingFra(
            no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging delvis) {

        return new DelvisTilrettelegging()
                .withTilrettelagtArbeidFom(delvis.getTilrettelagtArbeidFom())
                .withStillingsprosent(BigDecimal.valueOf(prosentFra(delvis.getStillingsprosent())));
    }

    private static HelTilrettelegging helTilretteleggingFra(
            no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging hel) {
        return new HelTilrettelegging()
                .withTilrettelagtArbeidFom(hel.getTilrettelagtArbeidFom());
    }

    private static double prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::getProsent)
                .orElse(0d);
    }

    private static List<JAXBElement<Object>> tilretteleggingVedleggFraIDs(List<String> vedlegg) {
        return safeStream(vedlegg)
                .map(s -> SVP_FACTORY_V1.createTilretteleggingVedlegg(new Vedlegg().withId(s)))
                .toList();
    }

    private static Arbeidsforhold arbeidsforholdFra(
            no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold forhold) {

        if (forhold instanceof no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet virksomhet) {
            return new Virksomhet()
                    .withIdentifikator(virksomhet.getOrgnr());
        }
        if (forhold instanceof no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver privat) {
            return new PrivatArbeidsgiver()
                    .withIdentifikator(privat.getFnr().getFnr());
        }

        if (forhold instanceof no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Frilanser frilanser) {
            return new Frilanser()
                    .withOpplysningerOmTilretteleggingstiltak(frilanser.getTilretteleggingstiltak())
                    .withOpplysningerOmRisikofaktorer(frilanser.getRisikoFaktorer());
        }

        if (forhold instanceof no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende selvstendig) {
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

    private static Map<no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold, List<no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.Tilrettelegging>> tilretteleggingByArbeidsforhold(
            List<no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.Tilrettelegging> tilretteleggingsPerioder) {
        Map<no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold, List<no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.Tilrettelegging>> tilretteleggingByArbeidsforhold = new HashMap<>();
        tilretteleggingsPerioder.forEach(tp -> tilretteleggingByArbeidsforhold
                .computeIfAbsent(tp.getArbeidsforhold(), key -> new ArrayList<>())
                .add(tp));
        return tilretteleggingByArbeidsforhold;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapperEgenskaper=" + mapperEgenskaper() + "]";
    }

}
