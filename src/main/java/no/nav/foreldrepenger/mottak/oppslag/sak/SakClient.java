package no.nav.foreldrepenger.mottak.oppslag.sak;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.http.RetryAware;

public interface SakClient extends RetryAware {
    List<Sak> sakerFor(AktørId aktor, String tema);
}
