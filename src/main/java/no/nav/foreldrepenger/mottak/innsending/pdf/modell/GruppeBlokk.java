package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import java.util.List;
import java.util.Objects;

public class GruppeBlokk extends Blokk {
    private final String overskrift;
    private final List<? extends Blokk> tabellRader;

    public GruppeBlokk(String overskrift, List<? extends Blokk> tabellRader) {
        this.overskrift = overskrift;
        this.tabellRader = tabellRader;
    }

    public String getOverskrift() {
        return overskrift;
    }

    public List<? extends Blokk> getTabellRader() {
        return tabellRader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GruppeBlokk that = (GruppeBlokk) o;
        return Objects.equals(overskrift, that.overskrift) && Objects.equals(tabellRader, that.tabellRader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(overskrift, tabellRader);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String overskrift;
        private List<? extends Blokk> tabellRader;

        Builder() {
        }

        public Builder overskrift(String overskrift) {
            this.overskrift = overskrift;
            return this;
        }

        public Builder tabellRader(List<? extends Blokk> tabellRader) {
            this.tabellRader = tabellRader;
            return this;
        }

        public GruppeBlokk build() {
            return new GruppeBlokk(this.overskrift, this.tabellRader);
        }
    }
}
