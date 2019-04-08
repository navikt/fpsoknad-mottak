package no.nav.foreldrepenger.mottak.innsyn;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BehandlingÅrsak {
    HENDELSE_FØDSEL("RE-HENDELSE-FØDSEL"),
    MANGLENDE_FØDSEL("RE-MF"),
    MANGLENDE_FØDSEL_TERMIN("RE-MFIP"),
    ULIKT_ANTALL_BARN("RE-AVAB"),
    FEIL_LOVANVENDELSE("RE-LOV"),
    FEIL_REGELVERKSFORSTÅELSE("RE-RGLF"),
    FEIL_ELLER_ENDRET_FAKTA("RE-FEFAKTA"),
    PROSESSURELL_FEIL("RE-PRSSL"),
    ETTER_KLAGE("ETTER_KLAGE"),
    ENDRING_FRA_BRUKER("RE-END-FRA-BRUKER"),
    ENDRET_INNTEKTSMELDING("RE-END-INNTEKTSMELD"),
    KØET_BEHANDLING("KØET_BEHANDLING"),
    BERØRT_BEHANDLING("BERØRT_BEHANDLING"),
    REGISTER_OPPLYSNING("RE-REGISTEROPPL"),
    YTELSE("RE-YTELSE"),
    KLAGE("RE-KLAG"),
    MEDLEMSKAP("RE-MDL"),
    OPPTJENING("RE-OPTJ"),
    FORDELING_STØNADSPERIODE("RE-FRDLING"),
    INNTEKT("RE-INNTK"),
    DØD("RE-DØD"),
    RELASJON_TIL_BARNET("RE-SRTB"),
    SØKNADSFRIST("RE-FRIST"),
    BEREGNINGSGRUNNLAG("RE-BER-GRUN"),
    TILSTØTENDE_YTELSE_INNVILGET("RE-TILST-YT-INNVIL"),
    ENDRING_BEREGNINGSGRUNNLAG("RE-ENDR-BER-GRUN"),
    TILSTØTENDE_YTELSE_OPPHØRT("RE-TILST-YT-OPPH"),
    ANNET("RE-ANNET");

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingÅrsak.class);

    @JsonValue
    private final String årsak;

    BehandlingÅrsak(String årsak) {
        this.årsak = årsak;
    }

    public static BehandlingÅrsak valueSafelyOf(final String årsak) {
        return Arrays.stream(values())
                .filter(v -> v.årsak.equals(årsak))
                .findFirst()
                .orElse(logNull(årsak));
    }

    private static BehandlingÅrsak logNull(String årsak) {
        LOG.warn("Ingen enum verdi for {}", årsak);
        return null;
    }
}
