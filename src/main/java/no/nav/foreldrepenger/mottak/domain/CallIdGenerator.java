package no.nav.foreldrepenger.mottak.domain;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class CallIdGenerator {

    private static final String NAV_CALL_ID = "Nav-CallId";

    public String getOrCreate() {
        return Optional.ofNullable(MDC.get(NAV_CALL_ID)).orElse(create());
    }

    public String create() {
        String value = UUID.randomUUID().toString();
        MDC.put(NAV_CALL_ID, value);
        return value;
    }
}
