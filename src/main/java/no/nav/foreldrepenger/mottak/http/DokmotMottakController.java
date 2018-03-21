package no.nav.foreldrepenger.mottak.http;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotJMSSender;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSendingsResultat;

@RestController
@RequestMapping(DokmotMottakController.DOKMOT)
public class DokmotMottakController {

    private static final Logger LOG = getLogger(DokmotMottakController.class);

    public static final String DOKMOT = "/mottak/dokmot";

    private final DokmotJMSSender sender;
    @Autowired
    DokmotEngangsstønadXMLGenerator søknadGenerator;
    @Autowired
    DokmotEngangsstønadXMLKonvoluttGenerator konvoluttGenerator;

    @Inject
    public DokmotMottakController(DokmotJMSSender sender) {
        this.sender = sender;
    }

    @GetMapping(value = "/ping", produces = APPLICATION_XML_VALUE)
    public ResponseEntity<String> ping(@RequestParam("navn") String navn) {
        LOG.info("I was pinged");
        return ResponseEntity.status(HttpStatus.OK).body("Hello " + navn);
    }

    @PostMapping(value = "/send", produces = APPLICATION_XML_VALUE)
    public ResponseEntity<String> send(@Valid @RequestBody Søknad søknad) {
        LOG.info("Sender søknad til DOKMOT {}", søknad);
        SøknadSendingsResultat result = sender.sendSøknad(søknad);
        return ResponseEntity.status(HttpStatus.OK).body(result.getRef());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sender=" + sender + "]";
    }
}
