package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public record OrganisasjonsNavnDTO(NavnDTO navn) {

    public String tilOrganisasjonsnavn() {
        return Stream.of(navn.navnelinje1(), navn.navnelinje2(), navn.navnelinje3(), navn.navnelinje4(), navn.navnelinje5())
            .filter(s -> s != null && !s.isEmpty())
            .collect(Collectors.joining(", "));
    }

    public record NavnDTO(String sammensattnavn, String navnelinje1, String navnelinje2, String navnelinje3, String navnelinje4, String navnelinje5) {
    }
}
