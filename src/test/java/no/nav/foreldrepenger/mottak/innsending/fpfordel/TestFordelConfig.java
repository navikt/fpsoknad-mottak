package no.nav.foreldrepenger.mottak.innsending.fpfordel;

public class TestFordelConfig extends FPFordelConfig {

    @Override
    public String getUri() {
        return "http://localhost:8089";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
