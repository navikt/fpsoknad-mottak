package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.List;

import lombok.Data;

@Data
public class Opptjening {

    private final List<Arbeidsforhold> arbeidsforhold;
    private final List<EgenNæring> egenNæring;
    private final List<AnnenOpptjening> annenOpptjening;

}
 