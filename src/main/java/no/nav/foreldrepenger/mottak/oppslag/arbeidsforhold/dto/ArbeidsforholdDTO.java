package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto;

import static no.nav.foreldrepenger.common.util.TimeUtil.nowWithinPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;

public record ArbeidsforholdDTO(ArbeidsgiverDTO arbeidsgiver,
                                AnsettelsesperiodeDTO ansettelsesperiode,
                                List<ArbeidsavtaleDTO> arbeidsavtaler) {

    public ProsentAndel gjeldendeStillingsprosent() {
        return arbeidsavtaler.stream()
            .filter(this::erGjeldende)
            .map(ArbeidsavtaleDTO::stillingsprosent)
            .findFirst()
            .orElse(null);
    }

    private boolean erGjeldende(ArbeidsavtaleDTO arbeidsavtaleDTO) {
        var fom = arbeidsavtaleDTO.gyldighetsperiode().fom();
        if (fom.isAfter(LocalDate.now())) {
            return true;
        }
        var tom = Optional.ofNullable(arbeidsavtaleDTO.gyldighetsperiode().tom())
            .orElse(LocalDate.now());
        return nowWithinPeriod(fom, tom);
    }
}
