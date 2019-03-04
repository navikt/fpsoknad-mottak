package no.nav.foreldrepenger.mottak.domain.felles;

import com.neovisionaries.i18n.CountryCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UtenlandskForelder extends AnnenForelder {

    @NotBlank
    private final String id;
    private final CountryCode land;
    private final String navn;
}
