package no.nav.foreldrepenger.mottak.innsending.varsel;

import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.LOCAL;
import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.TEST;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.person;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles({ LOCAL, TEST })
@EnableConfigurationProperties(VarselConfig.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        VarselSender.class,
        VoidVarselConnection.class,
        VarselJMSConfiguration.class,
        VarselXMLGenerator.class
})
@TestPropertySource(properties = {
        "varsel.enabled=false",
        "varsel.uri=mq://jalla:6666",
        "varsel.channelname=DEV.APP.SVRCONN",
        "varsel.hostname=localhost" }) // Avhenger av at man har satt opp mq container lokalt
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
