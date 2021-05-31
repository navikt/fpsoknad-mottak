package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.util.StringUtil.partialMask;

import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.mottak.domain.validation.OrgnrValidator;

public record Orgnummer(@JsonValue String orgnr) {

    public static final String MAGIC = "342352362";

    public static final Orgnummer MAGIC_ORG = Orgnummer.valueOf(MAGIC);

    public Orgnummer {

        if (!validator().isValid(orgnr, null)) {
            throw new IllegalArgumentException(orgnr + " er ikke et gyldig organisasjonsnummer");
        }
    }

    private OrgnrValidator validator() {
        return new OrgnrValidator();
    }

    public static Orgnummer valueOf(String orgnr) {
        return new Orgnummer(orgnr);
    }

    public String maskert() {
        return orgnr != null ? orgnr.substring(0, 5) + "****" : "<null>";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [orgnr=" + partialMask(orgnr, 9) + "]";
    }
}
