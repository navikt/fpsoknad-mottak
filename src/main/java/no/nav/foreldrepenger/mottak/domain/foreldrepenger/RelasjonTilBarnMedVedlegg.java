package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

@Data
@Valid
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = Fødsel.class, name = "fødsel"),
        @Type(value = Adopsjon.class, name = "adopsjon"),
        @Type(value = FremtidigFødsel.class, name = "termin"),
        @Type(value = Omsorgsovertakelse.class, name = "omsorgsovertakelse")
})
public abstract class RelasjonTilBarnMedVedlegg {

    private final List<Vedlegg> vedlegg;
    @Positive
    private final int antallBarn;

    public RelasjonTilBarnMedVedlegg(int antallBarn, List<Vedlegg> vedlegg) {
        this.antallBarn = antallBarn;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
    }
}
