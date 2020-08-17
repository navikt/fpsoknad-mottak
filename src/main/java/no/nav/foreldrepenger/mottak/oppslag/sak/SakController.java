package no.nav.foreldrepenger.mottak.oppslag.sak;

import static no.nav.foreldrepenger.mottak.util.Constants.FORELDREPENGER;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.foreldrepenger.mottak.http.ProtectedRestController;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@ProtectedRestController
public class SakController {

    public static final String SAK = "/sak";
    private final SakClient sakClient;
    private final Oppslag oppslag;

    @Inject
    public SakController(SakClient sakClient, Oppslag oppslag) {
        this.sakClient = sakClient;
        this.oppslag = oppslag;
    }

    @GetMapping(SAK)
    public List<Sak> saker(@RequestParam(name = "tema", defaultValue = FORELDREPENGER) String tema) {
        return sakClient.sakerFor(oppslag.aktørId(), tema);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sakClient=" + sakClient + ", oppslag=" + oppslag + "]";
    }

}
