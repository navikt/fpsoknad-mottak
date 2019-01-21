package no.nav.foreldrepenger.mottak.innsending.varsel;

import no.nav.foreldrepenger.mottak.util.jaxb.VarselJaxbUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    VarselJaxbUtil.class,
    VarselXMLGenerator.class //,
    //VarselQueueConfig.class,
    //VarselConnection.class
    })
class VarselXMLGeneratorTest {
    @Autowired
    VarselJaxbUtil jaxb;
    @Autowired
    VarselXMLGenerator varselXmlGenerator;
    //@Autowired
    //VarselSender sender;

    @Test
    void tilXml() {
        System.out.println(varselXmlGenerator.tilXml(person()));
    }

    //@Test
    //void leggPåKø() {
    //    sender.send(person());
    //}
}
