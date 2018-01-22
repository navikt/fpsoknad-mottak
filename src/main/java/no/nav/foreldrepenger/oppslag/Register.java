package no.nav.foreldrepenger.oppslag;

public enum Register {
    INFOTRYGD("Infotrygd"), FPSAK("FPSak"), INNTEKTSMELDING("Inntekt"), ARENA("Arena"), STS("STS"), AKTØR(
            "Aktørregisteret");

    private String displayValue;

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    Register(String displayValue) {
        this.displayValue = displayValue;
    }
}
