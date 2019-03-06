package no.nav.foreldrepenger.mottak.domain.felles.annenforelder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UkjentForelder extends AnnenForelder {

    @Override
    public boolean hasId() {
        return false;
    }
}
