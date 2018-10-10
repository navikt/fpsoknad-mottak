package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.http.Constants.NAV_CALL_ID;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class CallIdGenerator {

    public String getOrCreate() {
        return Optional.ofNullable(MDC.get(NAV_CALL_ID)).orElse(createAndPut());
    }

    public String createAndPut() {
        String value = UUID.randomUUID().toString();
        MDC.put(NAV_CALL_ID, value);
        return value;
    }
}
