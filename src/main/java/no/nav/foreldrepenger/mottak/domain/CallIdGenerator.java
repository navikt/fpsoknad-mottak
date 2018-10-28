package no.nav.foreldrepenger.mottak.domain;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class CallIdGenerator {

    public String create() {
        return UUID.randomUUID().toString();
    }
}
