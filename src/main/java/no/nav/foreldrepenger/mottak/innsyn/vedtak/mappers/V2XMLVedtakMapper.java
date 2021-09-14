package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.Constants.UKJENT_KODEVERKSVERDI;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.felles.BehandlingTema;
import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.SaksInformasjon;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.ArbeidType;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.AvslagsÅrsak;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.ManuellBehandlingsÅrsak;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.PeriodeAktivitet;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.UttaksPeriode;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.UttaksPeriodeResultatType;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.UttaksPeriodeResultatÅrsak;
import no.nav.foreldrepenger.mottak.util.jaxb.VedtakV2ESJAXBUtil;
import no.nav.foreldrepenger.mottak.util.jaxb.VedtakV2FPJAXBUtil;
import no.nav.vedtak.felles.xml.felles.v2.BooleanOpplysning;
import no.nav.vedtak.felles.xml.felles.v2.DateOpplysning;
import no.nav.vedtak.felles.xml.felles.v2.DecimalOpplysning;
import no.nav.vedtak.felles.xml.felles.v2.KodeverksOpplysning;
import no.nav.vedtak.felles.xml.felles.v2.PeriodeOpplysning;
import no.nav.vedtak.felles.xml.felles.v2.StringOpplysning;
import no.nav.vedtak.felles.xml.vedtak.uttak.fp.v2.UttakForeldrepenger;
import no.nav.vedtak.felles.xml.vedtak.uttak.fp.v2.UttaksresultatPeriode;
import no.nav.vedtak.felles.xml.vedtak.uttak.fp.v2.UttaksresultatPeriodeAktivitet;

@Component
public class V2XMLVedtakMapper implements XMLVedtakMapper {
    private static final Logger LOG = LoggerFactory.getLogger(V2XMLVedtakMapper.class);
    private static final VedtakV2FPJAXBUtil JAXB_FP = new VedtakV2FPJAXBUtil(true);
    private static final VedtakV2ESJAXBUtil JAXB_ES = new VedtakV2ESJAXBUtil(true);

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return new MapperEgenskaper(V2, INITIELL_FORELDREPENGER, INITIELL_ENGANGSSTØNAD);
    }

    @Override
    public Vedtak tilVedtak(String xml, SøknadEgenskap egenskap) {
        return switch (egenskap.getFagsakType()) {
            case FORELDREPENGER -> Optional.ofNullable(xml)
                    .map(V2XMLVedtakMapper::tilFPVedtak)
                    .orElse(null);
            case ENGANGSSTØNAD -> Optional.ofNullable(xml)
                    .map(V2XMLVedtakMapper::tilESVedtak)
                    .orElse(null);
            case SVANGERSKAPSPENGER -> {
                LOG.warn("Svangerskapspenger vedtak ikke støttet");
                yield null;
            }
            default -> throw new UnexpectedInputException("Ukjent fagsak type %s", egenskap.getFagsakType());
        };
    }

    private static Vedtak tilFPVedtak(String xml) {
        try {
            var saksInfo = tilSaksInformasjon(unmarshalFP(xml));
            return new Vedtak(tilUttak(((JAXBElement<UttakForeldrepenger>) unmarshalFP(xml).getBehandlingsresultat()
                    .getBeregningsresultat().getUttak().getAny().get(0)).getValue()), saksInfo);
        } catch (Exception e) {
            LOG.warn("Feil ved unmarshalling av vedtak {} for foreldrepenger", xml, e);
            return null;
        }
    }

    private static SaksInformasjon tilSaksInformasjon(no.nav.vedtak.felles.xml.vedtak.v2.Vedtak vedtak) {
        SaksInformasjon saksInfo = new SaksInformasjon();
        saksInfo.setAnsvarligBeslutterIdent(vedtak.getAnsvarligBeslutterIdent());
        saksInfo.setAnsvarligSaksbehandlerIdent(vedtak.getAnsvarligSaksbehandlerIdent());
        saksInfo.setBehandlendeEnhet(vedtak.getBehandlendeEnhet());
        saksInfo.setBehandlingsTema(tilBehandlingsTema(vedtak.getBehandlingsTema().getKode()));
        saksInfo.setFagsakAnnenForelderId(vedtak.getFagsakAnnenForelderId());
        saksInfo.setFagsakId(vedtak.getFagsakId());
        saksInfo.setFagsakType(vedtak.getFagsakType().getKode());
        saksInfo.setSøknadsdato(vedtak.getSoeknadsdato());
        saksInfo.setVedtaksdato(vedtak.getVedtaksdato());
        saksInfo.setKlagedato(vedtak.getKlagedato());
        return saksInfo;
    }

    private static BehandlingTema tilBehandlingsTema(String kode) {
        return Optional.ofNullable(kode)
                .map(BehandlingTema::valueOf)
                .orElse(null);
    }

    private static Vedtak tilESVedtak(String xml) {
        try {
            return new Vedtak(null, tilSaksInformasjon(unmarshalES(xml)));
        } catch (Exception e) {
            LOG.warn("Feil ved unmarshalling av vedtak {} for engangsstønad", xml, e);
            return null;
        }
    }

    private static no.nav.vedtak.felles.xml.vedtak.v2.Vedtak unmarshalFP(String xml) {
        return JAXB_FP.unmarshal(xml, no.nav.vedtak.felles.xml.vedtak.v2.Vedtak.class);
    }

    private static no.nav.vedtak.felles.xml.vedtak.v2.Vedtak unmarshalES(String xml) {
        return JAXB_ES.unmarshal(xml, no.nav.vedtak.felles.xml.vedtak.v2.Vedtak.class);
    }

    private static no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.Uttak tilUttak(UttakForeldrepenger uttak) {
        return new no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.Uttak(
                tilDato(uttak.getFoersteLovligeUttaksdag()),
                tilUttaksPerioder(uttak.getUttaksresultatPerioder()));
    }

    private static List<UttaksPeriode> tilUttaksPerioder(List<UttaksresultatPeriode> perioder) {
        return safeStream(perioder)
                .map(V2XMLVedtakMapper::tilUttaksPeriode)
                .toList();
    }

    private static LocalDate tilDato(DateOpplysning dato) {
        return Optional.ofNullable(dato)
                .map(DateOpplysning::getValue)
                .filter(Objects::nonNull)
                .orElse(null);
    }

    private static UttaksPeriode tilUttaksPeriode(UttaksresultatPeriode periode) {
        return new UttaksPeriode(tilPeriode(periode.getPeriode()),
                tilResultatType(periode.getPeriodeResultatType()),
                tilÅrsak(periode.getPerioderesultataarsak()),
                tilString(periode.getBegrunnelse()),
                tilPeriodeaktiviteter(periode.getUttaksresultatPeriodeAktiviteter()),
                tilBoolean(periode.getGraderingInnvilget()),
                tilBoolean(periode.getSamtidiguttak()),
                tilBoolean(periode.getManueltBehandlet()),
                tilManuellbehandlingsÅrsak(periode.getManuellbehandlingaarsak()));
    }

    private static UttaksPeriodeResultatType tilResultatType(KodeverksOpplysning type) {
        return kodeFra(type)
                .map(UttaksPeriodeResultatType::valueSafelyOf)
                .orElse(null);
    }

    private static ManuellBehandlingsÅrsak tilManuellbehandlingsÅrsak(KodeverksOpplysning årsak) {
        return kodeFra(årsak)
                .map(ManuellBehandlingsÅrsak::valueOf)
                .orElse(null);
    }

    private static List<PeriodeAktivitet> tilPeriodeaktiviteter(List<UttaksresultatPeriodeAktivitet> aktiviteter) {
        return safeStream(aktiviteter)
                .map(V2XMLVedtakMapper::tilPeriodeAktivitet)
                .toList();
    }

    private static PeriodeAktivitet tilPeriodeAktivitet(UttaksresultatPeriodeAktivitet aktivitet) {
        return new PeriodeAktivitet(tilString(aktivitet.getArbeidsforholdid()),
                tilProsent(aktivitet.getArbeidstidsprosent()),
                tilAvslagsÅrsak(aktivitet.getAvslagaarsak()),
                tilBoolean(aktivitet.getGradering()),
                // tilInt(aktivitet.getTrekkdager())
                0, // TODO
                tiltrekkonto(aktivitet.getTrekkkonto()),
                tilProsent(aktivitet.getUtbetalingsprosent()),
                tilArbeidType(aktivitet.getUttakarbeidtype()),
                tilString(aktivitet.getVirksomhet()));
    }

    private static AvslagsÅrsak tilAvslagsÅrsak(KodeverksOpplysning årsak) {
        return kodeFra(årsak)
                .map(AvslagsÅrsak::valueSafelyOf)
                .orElse(null);
    }

    private static ArbeidType tilArbeidType(KodeverksOpplysning type) {
        return kodeFra(type)
                .map(ArbeidType::valueSafelyOf)
                .orElse(null);
    }

    private static StønadskontoType tiltrekkonto(KodeverksOpplysning konto) {
        return kodeFra(konto)
                .map(StønadskontoType::valueSafelyOf)
                .orElse(null);
    }

    private static UttaksPeriodeResultatÅrsak tilÅrsak(KodeverksOpplysning årsak) {
        return Optional.ofNullable(årsak)
                .map(KodeverksOpplysning::getValue)
                .map(UttaksPeriodeResultatÅrsak::new)
                .orElse(null);
    }

    private static LukketPeriode tilPeriode(PeriodeOpplysning periode) {
        return Optional.ofNullable(periode)
                .map(p -> new LukketPeriode(p.getFom(), p.getTom()))
                .orElse(null);
    }

    private static Boolean tilBoolean(BooleanOpplysning value) {
        return Optional.ofNullable(value)
                .map(BooleanOpplysning::isValue)
                .orElse(Boolean.FALSE);
    }

    private static String tilString(StringOpplysning value) {
        return Optional.ofNullable(value)
                .map(StringOpplysning::getValue)
                .orElse(null);
    }

    private static Optional<String> kodeFra(KodeverksOpplysning opplysning) {
        return Optional.ofNullable(opplysning)
                .map(KodeverksOpplysning::getKode)
                .filter(Predicate.not(UKJENT_KODEVERKSVERDI::equals));
    }

    private static ProsentAndel tilProsent(DecimalOpplysning prosent) {
        return Optional.ofNullable(prosent)
                .map(DecimalOpplysning::getValue)
                .map(BigDecimal::doubleValue)
                .map(ProsentAndel::new)
                .orElse(null);
    }
}
