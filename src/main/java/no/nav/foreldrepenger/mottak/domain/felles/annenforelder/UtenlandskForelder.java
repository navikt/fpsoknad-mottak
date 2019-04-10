package no.nav.foreldrepenger.mottak.domain.felles.annenforelder;

import javax.validation.constraints.NotBlank;

import com.neovisionaries.i18n.CountryCode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UtenlandskForelder extends AnnenForelder {

    @NotBlank
    private final String id;
    @NotNull
    private final CountryCode land;
    private final String navn;

    @Override
    public boolean hasId() {
        return id != null;
    }
}
