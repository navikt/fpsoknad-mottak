package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;
import no.nav.foreldrepenger.mottak.util.jaxb.VedtakV2ESJAXBUtil;

@Component
public class V1XMLVedtakMapper implements XMLVedtakMapper {

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapperEgenskaper=" + mapperEgenskaper() + "]";
    }

    private static final Logger LOG = LoggerFactory.getLogger(V1XMLVedtakMapper.class);

    private static final VedtakV2ESJAXBUtil JAXB = new VedtakV2ESJAXBUtil(false, false);

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return new MapperEgenskaper(V1, INITIELL_ENGANGSSTØNAD, INITIELL_FORELDREPENGER);
    }

    @Override
    public Vedtak tilVedtak(String xml, SøknadEgenskap egenskap) {
        return Optional.ofNullable(xml)
                .map(x -> tilVedtak(x))
                .orElse(null);
    }

    private static Vedtak tilVedtak(String xml) {
        return null;
        /*
         * try { no.nav.vedtak.felles.xml.vedtak.v2.Vedtak vedtak = unmarshal(xml);
         * JAXBElement<UttakForeldrepenger> fp = (JAXBElement<UttakForeldrepenger>)
         * vedtak.getBehandlingsresultat()
         * .getBeregningsresultat().getUttak().getAny().get(0); return new
         * Vedtak(tilUttak(fp.getValue())); } catch (Exception e) {
         * LOG.warn("Feil ved unmarshalling av vedtak", e); return null;
         *
         * }
         */
    }

    /*
     * private static no.nav.vedtak.felles.xml.vedtak.v2.Vedtak unmarshal(String
     * xml) { return JAXB.unmarshal(xml,
     * no.nav.vedtak.felles.xml.vedtak.v2.Vedtak.class); }
     *
     * private static no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.Uttak
     * tilUttak(UttakForeldrepenger uttak) { return new
     * no.nav.foreldrepenger.mottak.innsyn.vedtak.uttak.Uttak(
     * tilDato(uttak.getFoersteLovligeUttaksdag()),
     * tilUttaksPerioder(uttak.getUttaksresultatPerioder())); }
     *
     * private static List<UttaksPeriode>
     * tilUttaksPerioder(List<UttaksresultatPeriode> perioder) { return
     * safeStream(perioder) .map(V2EngangsstønadXMLVedtakMapper::tilUttaksPeriode)
     * .collect(toList());
     *
     * }
     *
     * private static LocalDate tilDato(DateOpplysning dato) { return
     * Optional.ofNullable(dato) .map(DateOpplysning::getValue)
     * .filter(Objects::nonNull) .orElse(null); }
     *
     * private static UttaksPeriode tilUttaksPeriode(UttaksresultatPeriode periode)
     * { return new UttaksPeriode(tilPeriode(periode.getPeriode()),
     * tilResultatType(periode.getPeriodeResultatType()),
     * tilÅrsak(periode.getPerioderesultataarsak()),
     * tilString(periode.getBegrunnelse()),
     * tilPeriodeaktiviteter(periode.getUttaksresultatPeriodeAktiviteter()),
     * tilBoolean(periode.getGraderingInnvilget()),
     * tilBoolean(periode.getSamtidiguttak()),
     * tilBoolean(periode.getManueltBehandlet()),
     * tilManuellbehandlingsÅrsak(periode.getManuellbehandlingaarsak())); }
     *
     * private static UttaksPeriodeResultatType tilResultatType(KodeverksOpplysning
     * type) { return kodeFra(type) .map(UttaksPeriodeResultatType::valueSafelyOf)
     * .orElse(null); }
     *
     * private static ManuellBehandlingsÅrsak
     * tilManuellbehandlingsÅrsak(KodeverksOpplysning årsak) { return kodeFra(årsak)
     * .map(ManuellBehandlingsÅrsak::valueOf) .orElse(null); }
     *
     * private static List<PeriodeAktivitet>
     * tilPeriodeaktiviteter(List<UttaksresultatPeriodeAktivitet> aktiviteter) {
     * return safeStream(aktiviteter)
     * .map(V2EngangsstønadXMLVedtakMapper::tilPeriodeAktivitet) .collect(toList());
     * }
     *
     * private static PeriodeAktivitet
     * tilPeriodeAktivitet(UttaksresultatPeriodeAktivitet aktivitet) {
     *
     * return new PeriodeAktivitet(tilString(aktivitet.getArbeidsforholdid()),
     * tilProsent(aktivitet.getArbeidstidsprosent()),
     * tilAvslagsÅrsak(aktivitet.getAvslagaarsak()),
     * tilBoolean(aktivitet.getGradering()), tilInt(aktivitet.getTrekkdager()),
     * tiltrekkonto(aktivitet.getTrekkkonto()),
     * tilProsent(aktivitet.getUtbetalingsprosent()),
     * tilArbeidType(aktivitet.getUttakarbeidtype()),
     * tilString(aktivitet.getVirksomhet())); }
     *
     * private static AvslagsÅrsak tilAvslagsÅrsak(KodeverksOpplysning årsak) {
     * return kodeFra(årsak) .map(AvslagsÅrsak::valueSafelyOf) .orElse(null); }
     *
     * private static ArbeidType tilArbeidType(KodeverksOpplysning type) { return
     * kodeFra(type) .map(ArbeidType::valueSafelyOf) .orElse(null); }
     *
     * private static StønadskontoType tiltrekkonto(KodeverksOpplysning konto) {
     * return kodeFra(konto) .map(StønadskontoType::valueSafelyOf) .orElse(null); }
     *
     * private static UttaksPeriodeResultatÅrsak tilÅrsak(KodeverksOpplysning årsak)
     * { return Optional.ofNullable(årsak) .map(KodeverksOpplysning::getValue)
     * .map(UttaksPeriodeResultatÅrsak::new) .orElse(null); }
     *
     * private static LukketPeriode tilPeriode(PeriodeOpplysning periode) { return
     * Optional.ofNullable(periode) .map(p -> new LukketPeriode(p.getFom(),
     * p.getTom())) .orElse(null); }
     *
     * private static Integer tilInt(IntOpplysning value) { return
     * Optional.ofNullable(value) .map(IntOpplysning::getValue) .orElse(null); }
     *
     * private static Boolean tilBoolean(BooleanOpplysning value) { return
     * Optional.ofNullable(value) .map(BooleanOpplysning::isValue) .orElse(false); }
     *
     * private static String tilString(StringOpplysning value) { return
     * Optional.ofNullable(value) .map(StringOpplysning::getValue) .orElse(null); }
     *
     * private static Optional<String> kodeFra(KodeverksOpplysning opplysning) {
     * return Optional.ofNullable(opplysning) .map(KodeverksOpplysning::getKode)
     * .filter(not(k -> UKJENT_KODEVERKSVERDI.equals(k))); }
     *
     * private static ProsentAndel tilProsent(DecimalOpplysning prosent) { return
     * Optional.ofNullable(prosent) .map(DecimalOpplysning::getValue)
     * .map(BigDecimal::doubleValue) .map(ProsentAndel::new) .orElse(null); }
     */
}
