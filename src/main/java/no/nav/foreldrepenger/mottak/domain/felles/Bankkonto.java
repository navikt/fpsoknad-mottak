package no.nav.foreldrepenger.mottak.domain.felles;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static no.nav.foreldrepenger.mottak.util.StringUtil.mask;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = ANY)
public record Bankkonto(String kontonummer, String banknavn) {

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [kontonummer=" + mask(kontonummer) + ", banknavn=" + banknavn + "]";
    }
}
