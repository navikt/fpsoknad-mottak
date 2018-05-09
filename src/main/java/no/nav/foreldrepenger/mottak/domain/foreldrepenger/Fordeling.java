package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.List;

import lombok.Data;

@Data
public class Fordeling {

    private final boolean erAnnenForelderInformert;
    private final Overføringsårsak ønskerKvoteOverført;
    private final List<LukketPeriodeMedVedlegg> perioder;
}
