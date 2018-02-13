package no.nav.foreldrepenger.oppslag;

import org.junit.Test;

import no.nav.foreldrepenger.mottak.dokmot.DokmotXMLEnvelopeGenerator;

public class TestDokmotSerialization {

    @Test
    public void testEnvelope() throws Exception {
        String xml = new DokmotXMLEnvelopeGenerator().toXML(TestUtils.engangssøknad(false));
        System.out.println(xml);
    }

}
