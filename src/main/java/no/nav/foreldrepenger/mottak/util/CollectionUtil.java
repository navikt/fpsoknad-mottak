package no.nav.foreldrepenger.mottak.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

public final class CollectionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CollectionUtil.class);


    private CollectionUtil() {
    }


    public static <T> List<T> tryOrEmpty(Supplier<List<T>> c) {
        try {
            return c.get();
        } catch (Exception e) {
            LOG.info("Fikk exception, fortsetter med tom liste", e);
            return List.of();
        }
    }

}
