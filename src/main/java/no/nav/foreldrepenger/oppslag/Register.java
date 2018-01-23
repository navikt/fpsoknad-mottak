package no.nav.foreldrepenger.oppslag;

public enum Register {
    INFOTRYGD("Infotrygd"), FPSAK("FPSak"), INNTEKTSMELDING("Inntekt"), ARENA("Arena"), STS("STS"),
      AKTØR("Aktørregisteret"), AAREG("Arbeidsforhold");

    private String displayValue;

    public String getDisplayValue() {
        return displayValue;
    }

    Register(String displayValue) {
        this.displayValue = displayValue;
    }
}
