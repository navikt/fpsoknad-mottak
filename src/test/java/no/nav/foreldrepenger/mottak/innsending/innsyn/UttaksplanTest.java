package no.nav.foreldrepenger.mottak.innsending.innsyn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.common.domain.foreldrepenger.Dekningsgrad;
import no.nav.foreldrepenger.mottak.config.JacksonConfiguration;
import no.nav.foreldrepenger.mottak.innsyn.dto.UttaksplanDTO;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JacksonConfiguration.class)
class UttaksplanTest {

    @Autowired
    private ObjectMapper mapper;

    String test1 = "{\n" +
            "  \"termindato\" : \"2020-05-19\",\n" +
            "  \"fødselsdato\" : \"2020-05-19\",\n" +
            "  \"omsorgsovertakelsesdato\" : \"2020-05-19\",\n" +
            "  \"dekningsgrad\" : \"100\",\n" +
            "  \"antallBarn\" : 1,\n" +
            "  \"søkerErFarEllerMedmor\" : false,\n" +
            "  \"morErAleneOmOmsorg\" : false,\n" +
            "  \"morHarRett\" : false,\n" +
            "  \"morErUfør\" : false,\n" +
            "  \"annenPartHarRettPåForeldrepengerIEØS\" : false,\n" +
            "  \"ønskerJustertUttakVedFødsel\" : false,\n" +
            "  \"farMedmorErAleneOmOmsorg\" : false,\n" +
            "  \"farMedmorHarRett\" : false,\n" +
            "  \"annenForelderErInformert\" : false,\n" +
            "  \"uttaksPerioder\" : [ ]\n" +
            "}";
    String test2 = "{\n" +
            "  \"termindato\" : \"2020-05-19\",\n" +
            "  \"fødselsdato\" : \"2020-05-19\",\n" +
            "  \"omsorgsovertakelsesdato\" : \"2020-05-19\",\n" +
            "  \"dekningsgrad\" : 100,\n" +
            "  \"antallBarn\" : 1,\n" +
            "  \"søkerErFarEllerMedmor\" : false,\n" +
            "  \"morErAleneOmOmsorg\" : false,\n" +
            "  \"morHarRett\" : false,\n" +
            "  \"morErUfør\" : false,\n" +
            "  \"annenPartHarRettPåForeldrepengerIEØS\" : false,\n" +
            "  \"ønskerJustertUttakVedFødsel\" : false,\n" +
            "  \"farMedmorErAleneOmOmsorg\" : false,\n" +
            "  \"farMedmorHarRett\" : false,\n" +
            "  \"annenForelderErInformert\" : false,\n" +
            "  \"uttaksPerioder\" : [ ]\n" +
            "}";

    @Test
    public void testUttaksplanDTO() throws Exception {
        var dto = new UttaksplanDTO(LocalDate.of(2020, 5, 19), LocalDate.of(2020, 5, 19), LocalDate.of(2020, 5, 19),
            Dekningsgrad.HUNDRE, 1, false, false, false, false, false, false, false, false, false,
            Collections.emptyList());
        var dto1 = mapper.readValue(test1, UttaksplanDTO.class);
        assertEquals(dto, dto1);
        var dto2 = mapper.readValue(test2, UttaksplanDTO.class);
        assertEquals(dto, dto2);

    }
}
