package no.nav.foreldrepenger.errorhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class LoggingRetryListener extends RetryListenerSupport {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingRetryListener.class);

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
            Throwable throwable) {
        LOG.warn("Metode {} kastet exception {} for {} gang",
                context.getAttribute("context.name"),
                throwable.toString(), context.getRetryCount());
        super.onError(context, callback, throwable);
    }

}