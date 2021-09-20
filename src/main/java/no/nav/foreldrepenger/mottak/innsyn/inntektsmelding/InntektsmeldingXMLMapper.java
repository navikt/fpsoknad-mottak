package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.common.util.jaxb.AbstractJAXBUtil.tilBoolean;
import static no.nav.foreldrepenger.common.util.jaxb.AbstractJAXBUtil.tilDoubleFraBigDecimal;
import static no.nav.foreldrepenger.common.util.jaxb.AbstractJAXBUtil.tilDoubleFraBigInteger;
import static no.nav.foreldrepenger.common.util.jaxb.AbstractJAXBUtil.tilTekst;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.felles.LukketPeriode;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.util.jaxb.InntektsmeldingJAXBUtil;
import no.seres.xsd.nav.inntektsmelding_m._20180924.Avsendersystem;
import no.seres.xsd.nav.inntektsmelding_m._20180924.AvtaltFerieListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.DelvisFravaer;
import no.seres.xsd.nav.inntektsmelding_m._20180924.DelvisFravaersListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.EndringIRefusjon;
import no.seres.xsd.nav.inntektsmelding_m._20180924.EndringIRefusjonsListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.FravaersPeriodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.GjenopptakelseNaturalytelseListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.GraderingIForeldrepenger;
import no.seres.xsd.nav.inntektsmelding_m._20180924.GraderingIForeldrepengerListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.Inntekt;
import no.seres.xsd.nav.inntektsmelding_m._20180924.InntektsmeldingM;
import no.seres.xsd.nav.inntektsmelding_m._20180924.Kontaktinformasjon;
import no.seres.xsd.nav.inntektsmelding_m._20180924.NaturalytelseDetaljer;
import no.seres.xsd.nav.inntektsmelding_m._20180924.Omsorgspenger;
import no.seres.xsd.nav.inntektsmelding_m._20180924.OpphoerAvNaturalytelseListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.Periode;
import no.seres.xsd.nav.inntektsmelding_m._20180924.PleiepengerPeriodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20180924.Refusjon;
import no.seres.xsd.nav.inntektsmelding_m._20180924.SykepengerIArbeidsgiverperioden;
import no.seres.xsd.nav.inntektsmelding_m._20180924.UtsettelseAvForeldrepenger;
import no.seres.xsd.nav.inntektsmelding_m._20180924.UtsettelseAvForeldrepengerListe;

@Component
public final class InntektsmeldingXMLMapper {
    private static final InntektsmeldingJAXBUtil JAXB = new InntektsmeldingJAXBUtil();
    private static final Logger LOG = LoggerFactory.getLogger(InntektsmeldingXMLMapper.class);

    public static Inntektsmelding tilInntektsmelding(String xml) {
        if (xml == null) {
            LOG.debug("Ingen inntektsmelding ble funnet");
            return null;
        }
        try {
            var skjema = JAXB.unmarshalToElement(xml, InntektsmeldingM.class).getValue().getSkjemainnhold();
            return new Inntektsmelding(tilYtelse(skjema.getYtelse()),
                    tilInnsendingsÅrsak(skjema.getAarsakTilInnsending()),
                    tilArbeidsgiver(skjema.getArbeidsgiver()),
                    new Fødselsnummer(skjema.getArbeidstakerFnr()),
                    skjema.isNaerRelasjon(),
                    tilArbeidsforhold(skjema.getArbeidsforhold().getValue()),
                    tilRefusjon(skjema.getRefusjon()),
                    tilSykepenger(skjema.getSykepengerIArbeidsgiverperioden()),
                    tilDato(skjema.getStartdatoForeldrepengeperiode()),
                    tilOpphørNaturalYtelse(skjema.getOpphoerAvNaturalytelseListe()),
                    tilGjenopptakelseNatutalYtelse(skjema.getGjenopptakelseNaturalytelseListe()),
                    tilAvsender(skjema.getAvsendersystem()),
                    tilPleiepengePerioder(skjema.getPleiepengerPerioder()),
                    tilOmsorgsPenger(skjema.getOmsorgspenger()));
        } catch (Exception e) {
            LOG.debug("Feil ved unmarshalling av inntektsmelding", e);
            return null;
        }
    }

    private static OmsorgsPenger tilOmsorgsPenger(JAXBElement<Omsorgspenger> omsorgspenger) {
        if (omsorgspenger == null) {
            return null;
        }
        return new OmsorgsPenger(tilBoolean(omsorgspenger.getValue().getHarUtbetaltPliktigeDager()),
                tilFraværsPerioder(omsorgspenger.getValue().getFravaersPerioder()),
                tilDelvisFraværsPerioder(omsorgspenger.getValue().getDelvisFravaersListe()));
    }

    private static List<DelvisFraværsPeriode> tilDelvisFraværsPerioder(JAXBElement<DelvisFravaersListe> perioder) {
        if (perioder == null) {
            return emptyList();
        }
        return safeStream(perioder.getValue().getDelvisFravaer())
                .map(InntektsmeldingXMLMapper::tilDelvisFraværsPeriode)
                .toList();
    }

    private static DelvisFraværsPeriode tilDelvisFraværsPeriode(DelvisFravaer fravær) {
        return new DelvisFraværsPeriode(tilDoubleFraBigDecimal(fravær.getTimer()), tilDato(fravær.getDato()));
    }

    private static List<LukketPeriode> tilFraværsPerioder(JAXBElement<FravaersPeriodeListe> perioder) {
        if (perioder == null) {
            return emptyList();
        }
        return safeStream(perioder.getValue().getFravaerPeriode())
                .map(InntektsmeldingXMLMapper::tilLukketPeriode)
                .toList();
    }

    private static List<LukketPeriode> tilPleiepengePerioder(JAXBElement<PleiepengerPeriodeListe> pleiepenger) {
        if (pleiepenger == null) {
            return emptyList();
        }
        return safeStream(pleiepenger.getValue().getPeriode())
                .map(InntektsmeldingXMLMapper::tilLukketPeriode)
                .toList();
    }

    private static Avsender tilAvsender(Avsendersystem avsender) {
        return Optional.ofNullable(avsender)
                .map(a -> new Avsender(a.getSystemnavn(),
                        a.getSystemversjon(),
                        tilDatoOgDag(a.getInnsendingstidspunkt())))
                .orElse(null);
    }

    private static List<Naturalytelse> tilGjenopptakelseNatutalYtelse(
            JAXBElement<GjenopptakelseNaturalytelseListe> gjenopptakelser) {
        if (gjenopptakelser == null) {
            return emptyList();
        }
        return safeStream(gjenopptakelser.getValue().getNaturalytelseDetaljer())
                .map(InntektsmeldingXMLMapper::tilNaturalYtelse)
                .toList();
    }

    private static List<Naturalytelse> tilOpphørNaturalYtelse(JAXBElement<OpphoerAvNaturalytelseListe> opphør) {
        if (opphør == null) {
            return emptyList();
        }
        return safeStream(opphør.getValue().getOpphoerAvNaturalytelse())
                .map(InntektsmeldingXMLMapper::tilNaturalYtelse)
                .toList();
    }

    private static Naturalytelse tilNaturalYtelse(NaturalytelseDetaljer detaljer) {
        if (detaljer == null) {
            return null;
        }
        return new Naturalytelse(tilNaturalYtelseType(detaljer.getNaturalytelseType()),
                tilDoubleFraBigDecimal(detaljer.getBeloepPrMnd()), tilDato(detaljer.getFom()));
    }

    private static NaturalytelseType tilNaturalYtelseType(JAXBElement<String> type) {
        return type != null ? NaturalytelseType.valueOf(type.getValue()) : null;
    }

    private static SykepengerIArbeidsgiverPerioden tilSykepenger(
            JAXBElement<SykepengerIArbeidsgiverperioden> sykepenger) {
        if (sykepenger == null) {
            return null;
        }
        return new SykepengerIArbeidsgiverPerioden(null,
                tilDoubleFraBigDecimal(sykepenger.getValue().getBruttoUtbetalt()),
                tilTekst(sykepenger.getValue().getBegrunnelseForReduksjonEllerIkkeUtbetalt()));
    }

    private static no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.Refusjon tilRefusjon(
            JAXBElement<Refusjon> refusjon) {
        if (refusjon == null) {
            return null;
        }
        return new no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.Refusjon(
                tilDoubleFraBigDecimal(refusjon.getValue().getRefusjonsbeloepPrMnd()),
                tilDato(refusjon.getValue().getRefusjonsopphoersdato()),
                tilRefusjonsEndringer(refusjon.getValue().getEndringIRefusjonListe()));
    }

    private static List<RefusjonsEndring> tilRefusjonsEndringer(JAXBElement<EndringIRefusjonsListe> endringer) {
        if (endringer == null) {
            return emptyList();
        }
        return safeStream(endringer.getValue().getEndringIRefusjon())
                .map(InntektsmeldingXMLMapper::tilRefusjonsEndring)
                .toList();
    }

    private static RefusjonsEndring tilRefusjonsEndring(EndringIRefusjon endring) {
        return new RefusjonsEndring(
                tilDato(endring.getEndringsdato()),
                tilDoubleFraBigDecimal(endring.getRefusjonsbeloepPrMnd()));
    }

    private static no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.Arbeidsforhold tilArbeidsforhold(
            no.seres.xsd.nav.inntektsmelding_m._20180924.Arbeidsforhold forhold) {
        return new no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.Arbeidsforhold(
                tilId(forhold.getArbeidsforholdId()),
                tilDato(forhold.getFoersteFravaersdag()),
                tilInntekt(forhold.getBeregnetInntekt()),
                tilFeriePerioder(forhold.getAvtaltFerieListe()),
                tilUtsettelsesPerioder(forhold.getUtsettelseAvForeldrepengerListe()),
                tilGraderingsPerioder(forhold.getGraderingIForeldrepengerListe()));
    }

    private static List<GraderingsPeriode> tilGraderingsPerioder(JAXBElement<GraderingIForeldrepengerListe> perioder) {
        if (perioder == null) {
            return emptyList();
        }
        return safeStream(perioder.getValue().getGraderingIForeldrepenger())
                .map(InntektsmeldingXMLMapper::tilGraderingsPeriode)
                .toList();
    }

    private static GraderingsPeriode tilGraderingsPeriode(GraderingIForeldrepenger periode) {
        return new GraderingsPeriode(new ProsentAndel(tilDoubleFraBigInteger(periode.getArbeidstidprosent())),
                tilLukketPeriode(periode.getPeriode()));
    }

    private static LukketPeriode tilLukketPeriode(JAXBElement<Periode> periode) {
        return Optional.ofNullable(periode)
                .map(p -> p.getValue())
                .filter(Objects::nonNull)
                .map(InntektsmeldingXMLMapper::tilLukketPeriode)
                .orElse(null);
    }

    private static LukketPeriode tilLukketPeriode(Periode periode) {
        return tilLukketPeriode(periode.getFom(), periode.getTom());
    }

    private static LukketPeriode tilLukketPeriode(JAXBElement<LocalDate> fom, JAXBElement<LocalDate> tom) {
        return new LukketPeriode(tilDato(fom), tilDato(tom));
    }

    private static LocalDate tilDato(JAXBElement<LocalDate> dato) {
        return Optional.ofNullable(dato)
                .map(d -> d.getValue())
                .orElse(null);
    }

    private static LocalDateTime tilDatoOgDag(JAXBElement<LocalDateTime> dato) {
        return Optional.ofNullable(dato)
                .map(d -> d.getValue())
                .orElse(null);
    }

    private static List<UtsettelsesPeriode> tilUtsettelsesPerioder(JAXBElement<UtsettelseAvForeldrepengerListe> perioder) {
        if (perioder == null) {
            return emptyList();
        }
        return safeStream(perioder.getValue().getUtsettelseAvForeldrepenger())
                .map(InntektsmeldingXMLMapper::tilUtsettelsesPeriode)
                .toList();
    }

    private static UtsettelsesPeriode tilUtsettelsesPeriode(UtsettelseAvForeldrepenger periode) {
        return new UtsettelsesPeriode(tilLukketPeriode(periode.getPeriode()),
                tilUtsettelsesÅrsak(periode.getAarsakTilUtsettelse()));
    }

    private static UtsettelsesÅrsak tilUtsettelsesÅrsak(JAXBElement<String> årsak) {
        return Optional.ofNullable(årsak)
                .map(å -> å.getValue())
                .map(UtsettelsesÅrsak::valueOf)
                .orElse(null);
    }

    private static List<LukketPeriode> tilFeriePerioder(JAXBElement<AvtaltFerieListe> perioder) {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    private static String tilId(JAXBElement<String> id) {
        return Optional.ofNullable(id)
                .map(i -> i.getValue())
                .orElse(null);
    }

    private static no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.Inntekt tilInntekt(
            JAXBElement<Inntekt> inntekt) {
        return new no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.Inntekt(
                tilDoubleFraBigDecimal(inntekt.getValue().getBeloep()),
                tilÅrsak(inntekt.getValue().getAarsakVedEndring()));
    }

    private static BeregnetInntektEndringsÅrsak tilÅrsak(JAXBElement<String> årsak) {
        return Optional.ofNullable(årsak)
                .map(å -> å.getValue())
                .map(BeregnetInntektEndringsÅrsak::valueOf)
                .orElse(null);
    }

    private static Arbeidsgiver tilArbeidsgiver(
            no.seres.xsd.nav.inntektsmelding_m._20180924.Arbeidsgiver arbeidsgiver) {
        return new no.nav.foreldrepenger.mottak.innsyn.inntektsmelding.Arbeidsgiver(
                arbeidsgiver.getVirksomhetsnummer(),
                tilKontaktInformasjon(arbeidsgiver.getKontaktinformasjon()));
    }

    private static KontaktInformasjon tilKontaktInformasjon(Kontaktinformasjon kontaktinformasjon) {
        return new KontaktInformasjon(kontaktinformasjon.getKontaktinformasjonNavn(),
                kontaktinformasjon.getTelefonnummer());
    }

    private static InnsendingsÅrsak tilInnsendingsÅrsak(String årsak) {
        return Optional.ofNullable(årsak)
                .map(InnsendingsÅrsak::valueOf)
                .orElse(null);
    }

    private static Ytelse tilYtelse(String ytelse) {
        return Optional.ofNullable(ytelse)
                .map(Ytelse::valueOf)
                .orElse(null);
    }
}
