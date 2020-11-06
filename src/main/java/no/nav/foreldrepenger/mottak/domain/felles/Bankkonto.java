package no.nav.foreldrepenger.mottak.domain.felles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.util.StringUtil;

@Data
public class Bankkonto {

    private final String kontonummer;
    private final String banknavn;

    @JsonCreator
    public Bankkonto(@JsonProperty("kontonummer") String kontonummer,
            @JsonProperty("banknavn") String banknavn) {
        this.kontonummer = kontonummer;
        this.banknavn = banknavn;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [kontonummer=" + StringUtil.mask(kontonummer) + ", banknavn=" + banknavn + "]";
    }
}
