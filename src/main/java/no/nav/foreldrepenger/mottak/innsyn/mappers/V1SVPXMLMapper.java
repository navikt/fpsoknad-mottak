package no.nav.foreldrepenger.mottak.innsyn.mappers;

import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.mappers.V3XMLMapperCommon.tilMedlemsskap;
import static no.nav.foreldrepenger.mottak.innsyn.mappers.V3XMLMapperCommon.tilOpptjening;
import static no.nav.foreldrepenger.mottak.innsyn.mappers.V3XMLMapperCommon.tilSøker;
import static no.nav.foreldrepenger.mottak.innsyn.mappers.V3XMLMapperCommon.tilVedlegg;
import static no.nav.foreldrepenger.mottak.innsyn.mappers.V3XMLMapperCommon.ytelse;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.common.util.Versjon.V1;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.Tilrettelegging;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Frilanser;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet;
import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.jaxb.SVPV1JAXBUtil;
import no.nav.vedtak.felles.xml.soeknad.v3.OmYtelse;
import no.nav.vedtak.felles.xml.soeknad.v3.Soeknad;

@Component
public class V1SVPXMLMapper implements XMLSøknadMapper {
    private static final MapperEgenskaper EGENSKAPER = new MapperEgenskaper(V1, INITIELL_SVANGERSKAPSPENGER);
    private final SVPV1JAXBUtil jaxb;
    private static final Logger LOG = LoggerFactory.getLogger(V1SVPXMLMapper.class);

    @Inject
    public V1SVPXMLMapper() {
        this(false);
    }

    public V1SVPXMLMapper(boolean validate) {
        jaxb = new SVPV1JAXBUtil(validate);
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return EGENSKAPER;
    }

    @Override
    public Søknad tilSøknad(String xml, SøknadEgenskap egenskap) {
        return Optional.ofNullable(xml)
                .map(this::svpSøknad)
                .orElse(null);
    }

    private Søknad svpSøknad(String xml) {
        try {
            Soeknad søknad = jaxb.unmarshalToElement(xml, Soeknad.class).getValue();
            Søknad s = new Søknad(søknad.getMottattDato(), tilSøker(søknad.getSoeker()),
                    tilYtelse(søknad.getOmYtelse(), søknad.getMottattDato()),
                    tilVedlegg(søknad.getPaakrevdeVedlegg(), søknad.getAndreVedlegg()));
            s.setBegrunnelseForSenSøknad(søknad.getBegrunnelseForSenSoeknad());
            s.setTilleggsopplysninger(søknad.getTilleggsopplysninger());
            return s;
        } catch (Exception e) {
            LOG.debug("Feil ved unmarshalling av søknad {}, ikke kritisk foreløpig", EGENSKAPER, e);
            return null;
        }
    }

    private static Svangerskapspenger tilYtelse(OmYtelse omYtelse, LocalDate søknadsDato) {
        no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Svangerskapspenger søknad = ytelse(omYtelse,
                no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Svangerskapspenger.class);
        return new Svangerskapspenger(søknad.getTermindato(), søknad.getFødselsdato(),
                tilMedlemsskap(søknad.getMedlemskap(), søknadsDato), tilOpptjening(søknad.getOpptjening()),
                tilTilrettelegging(søknad.getTilretteleggingListe().getTilrettelegging()));
    }

    private static List<Tilrettelegging> tilTilrettelegging(
            List<no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Tilrettelegging> tilrettelegginger) {
        return safeStream(tilrettelegginger)
                .map(V1SVPXMLMapper::create)
                .flatMap(Collection::stream)
                .toList();
    }

    private static List<Tilrettelegging> create(
            no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Tilrettelegging tilrettelegging) {
        var tilretteleggingsliste = new ArrayList<Tilrettelegging>();
        tilretteleggingsliste.addAll(hel(tilrettelegging
                .getHelTilrettelegging(), tilrettelegging.getArbeidsforhold(),
                tilrettelegging.getBehovForTilretteleggingFom()));
        tilretteleggingsliste.addAll(delvis(tilrettelegging
                .getDelvisTilrettelegging(), tilrettelegging.getArbeidsforhold(),
                tilrettelegging.getBehovForTilretteleggingFom()));
        tilretteleggingsliste.addAll(ingen(tilrettelegging
                .getIngenTilrettelegging(), tilrettelegging.getArbeidsforhold(),
                tilrettelegging.getBehovForTilretteleggingFom()));
        return tilretteleggingsliste;
    }

    private static HelTilrettelegging hel(no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.HelTilrettelegging hel,
            no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Arbeidsforhold arbeidsforhold,
            LocalDate behovFra) {
        return Optional.ofNullable(hel)
                .map(h -> new HelTilrettelegging(tilArbeidsForhold(arbeidsforhold), behovFra,
                        h.getTilrettelagtArbeidFom(), null))
                .orElse(null);
    }

    private static List<HelTilrettelegging> hel(List<no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.HelTilrettelegging> hele,
            no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Arbeidsforhold arbeidsforhold,
            LocalDate behovFra) {
        return safeStream(hele)
                .map(h -> hel(h, arbeidsforhold, behovFra))
                .toList();
    }

    private static List<DelvisTilrettelegging> delvis(List<no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.DelvisTilrettelegging> delvise,
            no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Arbeidsforhold arbeidsforhold,
            LocalDate behovFra) {
        return safeStream(delvise)
                .map(d -> delvis(d, arbeidsforhold, behovFra))
                .toList();
    }

    private static List<IngenTilrettelegging> ingen(List<no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.IngenTilrettelegging> ingen,
            no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Arbeidsforhold arbeidsforhold,
            LocalDate behovFra) {
        return safeStream(ingen)
                .map(i -> ingen(i, arbeidsforhold, behovFra))
                .toList();
    }

    private static DelvisTilrettelegging delvis(
            no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.DelvisTilrettelegging delvis,
            no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Arbeidsforhold arbeidsforhold,
            LocalDate behovFra) {
        return Optional.ofNullable(delvis)
                .map(d -> new DelvisTilrettelegging(tilArbeidsForhold(arbeidsforhold), behovFra,
                        d.getTilrettelagtArbeidFom(), new ProsentAndel(d.getStillingsprosent().doubleValue()), null))
                .orElse(null);
    }

    private static IngenTilrettelegging ingen(
            no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.IngenTilrettelegging ingen,
            no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Arbeidsforhold arbeidsforhold,
            LocalDate behovFra) {
        return Optional.ofNullable(ingen)
                .map(i -> new IngenTilrettelegging(tilArbeidsForhold(arbeidsforhold), behovFra, i.getSlutteArbeidFom(),
                        null))
                .orElse(null);
    }

    private static Arbeidsforhold tilArbeidsForhold(
            no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Arbeidsforhold arbeidsforhold) {
        if (arbeidsforhold instanceof no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Frilanser frilanser) {
            return new Frilanser(frilanser.getOpplysningerOmRisikofaktorer(),
                    frilanser.getOpplysningerOmTilretteleggingstiltak());
        }
        if (arbeidsforhold instanceof no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.PrivatArbeidsgiver privatArbeidsgiver) {
            return new PrivatArbeidsgiver(new Fødselsnummer(privatArbeidsgiver.getIdentifikator()));
        }
        if (arbeidsforhold instanceof no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Arbeidsgiver arbeidsgiver) {
            return new Virksomhet(arbeidsgiver.getIdentifikator());
        }
        if (arbeidsforhold instanceof no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.SelvstendigNæringsdrivende selvstendig) {
            return new SelvstendigNæringsdrivende(selvstendig.getOpplysningerOmRisikofaktorer(),
                    selvstendig.getOpplysningerOmTilretteleggingstiltak());
        }
        throw new UnexpectedInputException("UKjent arbeidsforhold %s", arbeidsforhold.getClass().getSimpleName());
    }
}
