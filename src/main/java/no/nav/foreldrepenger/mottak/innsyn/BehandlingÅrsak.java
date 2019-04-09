package no.nav.foreldrepenger.mottak.innsyn;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public enum BehandlingÅrsak {
    HENDELSE_FØDSEL,
    MANGLENDE_FØDSEL,
    MANGLENDE_FØDSEL_TERMIN,
    ULIKT_ANTALL_BARN,
    FEIL_LOVANVENDELSE,
    FEIL_REGELVERKSFORSTÅELSE,
    FEIL_ELLER_ENDRET_FAKTA,
    PROSESSURELL_FEIL,
    ETTER_KLAGE,
    ENDRING_FRA_BRUKER,
    ENDRET_INNTEKTSMELDING,
    KØET_BEHANDLING,
    BERØRT_BEHANDLING,
    REGISTER_OPPLYSNING,
    YTELSE,
    KLAGE,
    MEDLEMSKAP,
    OPPTJENING,
    FORDELING_STØNADSPERIODE,
    INNTEKT,
    DØD,
    RELASJON_TIL_BARNET,
    SØKNADSFRIST,
    BEREGNINGSGRUNNLAG,
    TILSTØTENDE_YTELSE_INNVILGET,
    ENDRING_BEREGNINGSGRUNNLAG,
    TILSTØTENDE_YTELSE_OPPHØRT,
    ANNET;

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingÅrsak.class);

    
    public static BehandlingÅrsak valueSafelyOf(String name) {
        try {
            return BehandlingÅrsak.valueOf(name);
        } catch (Exception e) {
            LOG.warn("Ingen enum verdi for {}", name);
            return null;
        }
    }
}
