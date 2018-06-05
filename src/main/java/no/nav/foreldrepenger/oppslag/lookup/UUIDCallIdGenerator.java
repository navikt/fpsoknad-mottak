package no.nav.foreldrepenger.oppslag.lookup;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UUIDCallIdGenerator {

    private final String key;

    @Inject
    public UUIDCallIdGenerator(@Value("${callid.key:Nav-CallId}") String key) {
        this.key = key;
    }

    public String getOrCreate() {
        return Optional.ofNullable(MDC.get(key)).orElse(create());
    }

    public String create() {
        return UUID.randomUUID().toString();
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [key=" + key + "]";
    }

}
