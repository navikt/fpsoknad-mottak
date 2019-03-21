package no.nav.foreldrepenger.mottak.domain.svangerskapspenger;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.serialize;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.svp;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.V1SvangerskapspengerDomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.XMLInspektør;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.util.Versjon;

@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
public class TestSvangerskapsepengerSerialization {

    private static final Logger LOG = LoggerFactory.getLogger(TestSvangerskapsepengerSerialization.class);

    @Autowired
    ObjectMapper mapper;

    private static final XMLInspektør inspektør = new XMLStreamSøknadInspektør();

    private static final DomainMapper DOMAINMAPPER = new V1SvangerskapspengerDomainMapper();

    @Test
    public void inspiser() {
        SøknadEgenskap resultat = inspektør.inspiser(svp());
        assertEquals(resultat.getVersjon(), V1);
        assertEquals(resultat.getType(), INITIELL_SVANGERSKAPSPENGER);
    }

    @Test
    public void testSVP() {
        test(svp(), true);
    }

    @Test
    public void testXML() {
        Søknad svp = svp();
        String xml = DOMAINMAPPER.tilXML(svp, new AktorId("42"), inspektør.inspiser(svp));
        System.out.println(xml);
        SøknadEgenskap resultat = inspektør.inspiser(xml);
        assertEquals(Versjon.V1, resultat.getVersjon());
        assertEquals(SøknadType.INITIELL_SVANGERSKAPSPENGER, resultat.getType());
    }

    private void test(Object object, boolean print) {
        test(object, print, mapper);
    }

    void test(Object object) {
        test(object, false);

    }

    public static void test(Object expected, boolean log, ObjectMapper mapper) {
        try {
            if (expected == null) {
                return;
            }
            String serialized = serialize(expected, log, mapper);
            Object deserialized = mapper.readValue(serialized, expected.getClass());
            if (log) {
                LOG.info("{}", expected);
                LOG.info("{}", serialized);
                LOG.info("{}", deserialized);
            }
            assertEquals(expected, deserialized);
        } catch (IOException e) {
            LOG.error("{}", e);
            fail(expected.getClass().getSimpleName() + " failed");
        }
    }

}
