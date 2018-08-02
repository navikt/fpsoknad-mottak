package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import com.neovisionaries.i18n.CountryCode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class UtenlandskForelder extends AnnenForelder {

    private final String id;
    private final CountryCode land;
    private final String fornavn;
}
