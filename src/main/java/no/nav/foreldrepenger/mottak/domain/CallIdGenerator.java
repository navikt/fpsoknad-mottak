package no.nav.foreldrepenger.mottak.domain;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CallIdGenerator {

    private final String callIdKey;

    @Inject
    public CallIdGenerator(@Value("${callid.key:Nav-CallId}") String callIdKey) {
        this.callIdKey = callIdKey;
    }

    public String getOrCreate() {
        return Optional.ofNullable(MDC.get(callIdKey)).orElse(create());
    }

    public String create() {
        return doCreate();
    }

    private static String doCreate() {
        return UUID.randomUUID().toString();
    }

    public String getCallIdKey() {
        return callIdKey;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [callIdKey=" + callIdKey + "]";
    }

}
