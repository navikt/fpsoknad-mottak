package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.RelasjonTilBarnMedVedlegg;

@Data
@JsonPropertyOrder({ "tidligereOppholdsInfo", "framtidigOppholdsInfo" })
public class Medlemsskap {

    @Valid
    private final TidligereOppholdsInformasjon tidligereOppholdsInfo;
    @Valid
    private final FramtidigOppholdsInformasjon framtidigOppholdsInfo;

    public boolean varUtenlands(LocalDate day) {
        return day == null ? false : tidligereOppholdsInfo.varUtenlands(day);
    }

    public boolean skalVæreUtenlands(LocalDate day) {
        return day == null ? false : framtidigOppholdsInfo.skalVæreUtenlands(day);
    }

    public boolean utenlands(RelasjonTilBarnMedVedlegg relasjonTilBarn) {
        if (relasjonTilBarn instanceof Fødsel) {
            return varUtenlands(Fødsel.class.cast(relasjonTilBarn).getFødselsdato().get(0));
        }
        if (relasjonTilBarn instanceof FremtidigFødsel) {
            return skalVæreUtenlands(FremtidigFødsel.class.cast(relasjonTilBarn).getTerminDato());
        }
        if (relasjonTilBarn instanceof Adopsjon) {
            LocalDate dato = Adopsjon.class.cast(relasjonTilBarn).getOmsorgsovertakelsesdato();
            return dato.isAfter(LocalDate.now()) ? skalVæreUtenlands(dato) : varUtenlands(dato);
        }
        return false;
    }
}
