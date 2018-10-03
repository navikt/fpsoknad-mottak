package no.nav.foreldrepenger.mottak.domain;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class CallIdGenerator {

    public String getOrCreate() {
        return Optional.ofNullable(MDC.get("Nav-CallId")).orElse(create());
    }

    public String create() {
        return UUID.randomUUID().toString();
    }
}
