package no.nav.foreldrepenger.mottak.domain.felles.medlemskap;

import static com.neovisionaries.i18n.CountryCode.NO;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    public boolean varUtenlands(LocalDate dato) {
        return !varINorge(dato);
    }

    public boolean varINorge(LocalDate dato) {
        return NO.equals(landVedDato(dato));
    }

    public CountryCode landVedDato(LocalDate dato) {
        return Optional.ofNullable(dato)
                .map(d -> landVedDato(utenlandsOpphold(), d))
                .orElse(NO);
    }

    private static CountryCode landVedDato(List<Utenlandsopphold> utenlandsopphold, LocalDate dato) {
        return safeStream(utenlandsopphold)
                .filter(s -> s.varighet().isWithinPeriod(dato))
                .map(Utenlandsopphold::land)
                .findFirst()
                .orElse(NO);
    }

    public List<Utenlandsopphold> utenlandsOpphold() {
        return Stream
                .concat(safeStream(tidligereOppholdsInfo.getUtenlandsOpphold()),
                        safeStream(framtidigOppholdsInfo.getUtenlandsOpphold()))
                .toList();
    }
}
