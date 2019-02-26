package no.nav.foreldrepenger.mottak.innsending.varsel;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("dev")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        VarselSender.class,
        VarselConfig.class,
        VoidVarselConnection.class,
        VarselConfiguration.class,
        VarselXMLGenerator.class
})
@TestPropertySource(properties = {
        "varsel.queueName=DEV.QUEUE.1",
        "varsel.port=1414",
        "varsel.channelname=DEV.APP.SVRCONN",
        "varsel.hostname=localhost",
        "varsel.name=QM1" }) // Avhenger av at man har satt opp mq container lokalt
class VarselSenderTest {

    @Autowired
    VarselSender varselSender;

    @Test
    void varselSenderGeneratesAndSendsVarsel() {
        varselSender.varsle(new Varsel(LocalDateTime.now(), person()));
        // TODO: skriv en faktisk test her, denne hjelper kun for manuell kontroll på kø
        // dersom man kjører mq lokalt

    }

}
