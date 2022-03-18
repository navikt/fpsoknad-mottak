package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto;

import com.google.common.base.Joiner;


public record OrganisasjonsNavnDTO(NavnDTO navn) {

    public String tilOrganisasjonsnavn() {
        return Joiner.on(", ")
            .skipNulls()
            .join(navn.navnelinje1(), navn.navnelinje2(), navn.navnelinje3(), navn.navnelinje4(), navn.navnelinje5());
    }
}
