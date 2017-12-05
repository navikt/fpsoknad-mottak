package no.nav.foreldrepenger.selvbetjening;

import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.*;
import org.springframework.ws.client.core.support.*;

public class AktorClient extends WebServiceGatewaySupport {

    public HentAktoerIdForIdentResponse aktorIdForFnr(String fnr) {

        HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
        request.setIdent(fnr);

        HentAktoerIdForIdentResponse response = (HentAktoerIdForIdentResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://something", request);

        return response;
    }

}
