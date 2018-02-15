package no.nav.foreldrepenger.oppslag;

import org.junit.Test;

import no.nav.foreldrepenger.mottak.dokmot.DokmotXMLKonvoluttGenerator;

public class TestDokmotSerialization {

    @Test
    public void testEnvelope() throws Exception {
        String xml = new DokmotXMLKonvoluttGenerator().toXML(TestUtils.engangssøknad(false));
        System.out.println(xml);
    }

}
