package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.config.MessageSourceConfiguration.KVITTERINGSTEKSTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.mottak.config.MessageSourceConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MessageSourceConfiguration.class})
class SøknadTextFormatterTest {

    @Autowired
    @Qualifier(KVITTERINGSTEKSTER)
    private MessageSource kvitteringstekster;

    @Test
    void capitalize() {
        assertEquals("Enum to string", new SøknadTextFormatter(null, null).capitalize("ENUM_TO_STRING"));
    }

    @Test
    void datesMayBeNull() {
        assertEquals("", new SøknadTextFormatter(null, null).dato(null));
    }

    @Test
    void sammensattNavnFulltNavn() {
        var søknadTextFormatter = new SøknadTextFormatter(null, null);
        var navn = new Navn("Fornavn", "Mellomnavn", "Etternavn");

        var sammensattNavnFraNavn = søknadTextFormatter.sammensattNavn(navn);

        assertThat(sammensattNavnFraNavn).isEqualTo("Fornavn Mellomnavn Etternavn");
    }

    @Test
    void sammensattNavnIngenMellomnavn() {
        var søknadTextFormatter = new SøknadTextFormatter(null, null);
        var navn = new Navn("Fornavn", null, "Etternavn");

        var sammensattNavnFraNavn = søknadTextFormatter.sammensattNavn(navn);

        assertThat(sammensattNavnFraNavn).isEqualTo("Fornavn Etternavn");
    }

    @Test
    void sammensattNavnFraNavnNull() {
        var søknadTextFormatter = new SøknadTextFormatter(null, null);
        var navn = new Navn("", null, null);

        var sammensattNavnFraNavn = søknadTextFormatter.sammensattNavn(navn);

        assertThat(sammensattNavnFraNavn).isEmpty();
    }

    @Test
    void skalLoggeManglendeTekstnøkler() {
        var formatterer = new SøknadTextFormatter(null, kvitteringstekster);

        Logger logger = (Logger) LoggerFactory.getLogger(SøknadTextFormatter.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        formatterer.fromMessageSource("ikke-eksisterende-nøkkel");

        assertThat(listAppender.list).isNotEmpty();
        assertThat(listAppender.list.getFirst().getFormattedMessage()).contains("Finner ikke nøkkel 'ikke-eksisterende-nøkkel'");

        logger.detachAppender(listAppender);
    }
}
