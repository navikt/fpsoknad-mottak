package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.Constants.UKJENT_KODEVERKSVERDI;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.not;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
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
import no.nav.vedtak.felles.xml.felles.v2.IntOpplysning;
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
        switch (egenskap.getFagsakType()) {
        case FORELDREPENGER:
            return Optional.ofNullable(xml)
                    .map(x -> tilFPVedtak(x))
                    .orElse(null);
        case ENGANGSSTØNAD:
            return Optional.ofNullable(xml)
                    .map(x -> tilESVedtak(x))
                    .orElse(null);
        case SVANGERSKAPSPENGER:
            LOG.warn("Svangerskapspenger vedtak ikke støttet");
            return null;
        default:
            throw new UnexpectedInputException("Ukjent fagsak type %s", egenskap.getFagsakType());
        }
    }

    private static Vedtak tilFPVedtak(String xml) {
        try {
            no.nav.vedtak.felles.xml.vedtak.v2.Vedtak vedtak = unmarshalFP(xml);
            JAXBElement<UttakForeldrepenger> fp = (JAXBElement<UttakForeldrepenger>) vedtak.getBehandlingsresultat()
                    .getBeregningsresultat().getUttak().getAny().get(0);
            return new Vedtak(tilUttak(fp.getValue()));
        } catch (Exception e) {
            LOG.warn("Feil ved unmarshalling av vedtak {} for foreldrepenger", xml, e);
            return null;
        }
    }

    private static Vedtak tilESVedtak(String xml) {
        try {
            no.nav.vedtak.felles.xml.vedtak.v2.Vedtak vedtak = unmarshalES(xml);
            return new Vedtak(null);
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
                .collect(toList());

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
                .collect(toList());
    }

    private static PeriodeAktivitet tilPeriodeAktivitet(UttaksresultatPeriodeAktivitet aktivitet) {

        return new PeriodeAktivitet(tilString(aktivitet.getArbeidsforholdid()),
                tilProsent(aktivitet.getArbeidstidsprosent()),
                tilAvslagsÅrsak(aktivitet.getAvslagaarsak()),
                tilBoolean(aktivitet.getGradering()),
                tilInt(aktivitet.getTrekkdager()),
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

    private static Integer tilInt(IntOpplysning value) {
        return Optional.ofNullable(value)
                .map(IntOpplysning::getValue)
                .orElse(null);
    }

    private static Boolean tilBoolean(BooleanOpplysning value) {
        return Optional.ofNullable(value)
                .map(BooleanOpplysning::isValue)
                .orElse(false);
    }

    private static String tilString(StringOpplysning value) {
        return Optional.ofNullable(value)
                .map(StringOpplysning::getValue)
                .orElse(null);
    }

    private static Optional<String> kodeFra(KodeverksOpplysning opplysning) {
        return Optional.ofNullable(opplysning)
                .map(KodeverksOpplysning::getKode)
                .filter(not(k -> UKJENT_KODEVERKSVERDI.equals(k)));
    }

    private static ProsentAndel tilProsent(DecimalOpplysning prosent) {
        return Optional.ofNullable(prosent)
                .map(DecimalOpplysning::getValue)
                .map(BigDecimal::doubleValue)
                .map(ProsentAndel::new)
                .orElse(null);
    }
}
