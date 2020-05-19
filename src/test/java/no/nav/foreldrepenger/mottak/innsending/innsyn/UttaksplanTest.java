package no.nav.foreldrepenger.mottak.innsending.innsyn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.dto.UttaksplanDTO;

@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
public class UttaksplanTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testUttaksplanDTO() throws Exception {
        var dto = new UttaksplanDTO(LocalDate.now(), LocalDate.now(), LocalDate.now(), Dekningsgrad.GRAD100, 1, false,
                false, false, false, false, false, false, Collections.emptyList());
        var dto1 = mapper.readValue(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto),
                UttaksplanDTO.class);
        assertEquals(dto, dto1);

    }
}
