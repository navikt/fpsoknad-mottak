package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.AbstractInspektør.SØKNAD;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.graphql.Book;
import no.nav.foreldrepenger.mottak.http.UnprotectedRestController;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;

@UnprotectedRestController(value = MottakDevController.INNSENDING_PREPROD, produces = APPLICATION_XML_VALUE)
public class MottakDevController {

    @Autowired
    private GraphQLWebClient ql;

    private static final AktørId SØKER = new AktørId("42");

    public static final String INNSENDING_PREPROD = "/preprod";

    private final DomainMapper mapper;
    private final Inspektør inspektør;

    public MottakDevController(@Qualifier(DELEGERENDE) DomainMapper mapper,
            @Qualifier(SØKNAD) Inspektør inspektør) {
        this.mapper = mapper;
        this.inspektør = inspektør;
    }

    @PostMapping("/søknad")
    public String FPsøknad(@Valid @RequestBody Søknad søknad) {
        return fpSøknad(søknad);
    }

    @GetMapping(name = "/ql", produces = APPLICATION_JSON_VALUE)
    public Book ql(@RequestParam(name = "id") String id) {
        return ql.post("query-book.graphql", Map.of("id", id), Book.class).block();
    }

    @GetMapping(value = "/test", produces = APPLICATION_JSON_VALUE)
    public AktørId test() {
        return SØKER;
    }

    @PostMapping("/endringssøknad")
    public String FPendringssøknad(@Valid @RequestBody Endringssøknad endringssøknad) {
        return fpEndringsSøknad(endringssøknad);
    }

    private String fpSøknad(Søknad søknad) {
        return mapper.tilXML(søknad, SØKER, inspektør.inspiser(søknad));
    }

    private String fpEndringsSøknad(Endringssøknad endringssøknad) {
        return mapper.tilXML(endringssøknad, SØKER, inspektør.inspiser(endringssøknad));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + "]";
    }
}
