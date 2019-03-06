package no.nav.foreldrepenger.mottak.domain.felles;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.AnnenForelder;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UkjentForelder extends AnnenForelder {

    @Override
    public boolean hasId() {
        return false;
    }
}
