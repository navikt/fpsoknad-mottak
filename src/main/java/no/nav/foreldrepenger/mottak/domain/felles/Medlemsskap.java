package no.nav.foreldrepenger.mottak.domain.felles;

import static com.neovisionaries.i18n.CountryCode.NO;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;

@Data
@JsonPropertyOrder({ "tidligereOppholdsInfo", "framtidigOppholdsInfo" })
public class Medlemsskap {

    @Valid
    private final TidligereOppholdsInformasjon tidligereOppholdsInfo;
    @Valid
    private final FramtidigOppholdsInformasjon framtidigOppholdsInfo;

    public CountryCode landVedDato(LocalDate dato) {
        return dato == null ? NO : landVedDato(utenlandsOpphold(), dato);
    }

    private static CountryCode landVedDato(List<Utenlandsopphold> utenlandsopphold, LocalDate dato) {
        return utenlandsopphold
                .stream()
                .filter(s -> s.getVarighet().isWithinPeriod(dato))
                .map(s -> s.getLand())
                .findFirst()
                .orElse(NO);
    }

    public List<Utenlandsopphold> utenlandsOpphold() {
        return Stream
                .concat(safeStream(tidligereOppholdsInfo.getUtenlandsOpphold()),
                        safeStream(framtidigOppholdsInfo.getUtenlandsOpphold()))
                .collect(toList());
    }
}
