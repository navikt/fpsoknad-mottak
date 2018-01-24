package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class UtenlandskForelder extends KjentForelder {

    private final CountryCode land;

    @Builder
    @ConstructorProperties({ "lever", "land" })
    public UtenlandskForelder(boolean lever, CountryCode land) {
        super(lever);
        this.land = land;
    }

}
