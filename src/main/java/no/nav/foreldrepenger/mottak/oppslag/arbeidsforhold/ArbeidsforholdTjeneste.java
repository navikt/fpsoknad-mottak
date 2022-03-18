package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.CONFIDENTIAL;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.http.RetryAware;

@Service
public class ArbeidsforholdTjeneste implements RetryAware, ArbeidsInfo {
    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdTjeneste.class);
    private final ArbeidsforholdConnection connection;
    private final ArbeidsforholdNyConnection connectionNy;
    private final OrganisasjonConnection orgConnection;


    public ArbeidsforholdTjeneste(ArbeidsforholdConnection connection,
                                  ArbeidsforholdNyConnection connectionNy,
                                  OrganisasjonConnection orgConnection) {
        this.connection = connection;
        this.connectionNy = connectionNy;
        this.orgConnection = orgConnection;
    }

    @Override
    public List<EnkeltArbeidsforhold> hentArbeidsforhold() {
        var arbeidsforholdFraGammelWebclient = connection.hentArbeidsforhold();

        try {
            var arbeidsforholdFraNyWebclient = connectionNy.hentArbeidsforhold();
            if (!arbeidsforholdFraGammelWebclient.equals(arbeidsforholdFraNyWebclient)) {
                LOG.warn("Avvik mellom ny og gammel arbeidsforhold connection funnet!");
                LOG.warn(CONFIDENTIAL, "Gammel {} og ny {}", arbeidsforholdFraGammelWebclient, arbeidsforholdFraNyWebclient);
            }
        } catch (Exception e) {
            LOG.warn("Noe gikk galt med henting av arbeidsforhold på ny webklient: ", e);
        }
        return arbeidsforholdFraGammelWebclient;
    }

    @Override
    public String orgnavn(String orgnr) {
        return orgConnection.navn(orgnr);
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", orgConnection=" + orgConnection + "]";
    }
}
