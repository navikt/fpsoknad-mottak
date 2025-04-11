package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrganisasjonsNavnDTOTest {

    @Test
    void test1() {
        var navn = new OrganisasjonsNavnDTO.NavnDTO("sammensatt", "navnelinje1", "navnelinje2", "navnelinje3", "navnelinje4", "navnelinje5");
        var organisasjonsNavnDTO = new OrganisasjonsNavnDTO(navn);

        assertThat(organisasjonsNavnDTO.tilOrganisasjonsnavn())
            .isEqualTo("navnelinje1 navnelinje2 navnelinje3 navnelinje4 navnelinje5");
    }

    @Test
    void test2() {
        var navn = new OrganisasjonsNavnDTO.NavnDTO("sammensatt", "navnelinje1", "navnelinje2", null, "", null);
        var organisasjonsNavnDTO = new OrganisasjonsNavnDTO(navn);

        assertThat(organisasjonsNavnDTO.tilOrganisasjonsnavn())
            .isEqualTo("navnelinje1 navnelinje2");
    }

    @Test
    void test3() {
        var navn = new OrganisasjonsNavnDTO.NavnDTO("sammensatt", null, null, "navnelinje3", null, null);
        var organisasjonsNavnDTO = new OrganisasjonsNavnDTO(navn);

        assertThat(organisasjonsNavnDTO.tilOrganisasjonsnavn())
            .isEqualTo("navnelinje3");
    }

    @Test
    void test4() {
        var navn = new OrganisasjonsNavnDTO.NavnDTO(null,null, null, null, null, null);
        var organisasjonsNavnDTO = new OrganisasjonsNavnDTO(navn);

        assertThat(organisasjonsNavnDTO.tilOrganisasjonsnavn())
            .isNotNull()
            .isEmpty();
    }

}
