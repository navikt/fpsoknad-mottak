package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.Periode;

@Data
@Periode
@JsonPropertyOrder({ "fom", "tom" })
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = OverføringsPeriode.class, name = "overføring"),
        @Type(value = UttaksPeriode.class, name = "uttak"),
        @Type(value = OppholdsPeriode.class, name = "opphold"),
        @Type(value = UtsettelsesPeriode.class, name = "utsettelse")
})
public abstract class LukketPeriodeMedVedlegg {

    private final LocalDate fom;
    private final LocalDate tom;
    private final List<String> vedlegg;
}
