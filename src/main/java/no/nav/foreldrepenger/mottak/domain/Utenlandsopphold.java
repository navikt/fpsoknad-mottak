package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;

import com.neovisionaries.i18n.CountryCode;

import lombok.Data;

@Data
public class Utenlandsopphold {

    private final CountryCode land;
    private final Varighet varighet;

    @ConstructorProperties({ "land", "varighet" })
    public Utenlandsopphold(CountryCode land, Varighet varighet) {
        this.land = land;
        this.varighet = varighet;
    }

}
