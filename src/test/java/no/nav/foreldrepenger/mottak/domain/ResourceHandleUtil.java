package no.nav.foreldrepenger.mottak.domain;

import static org.springframework.util.StreamUtils.copyToByteArray;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.Resource;

public class ResourceHandleUtil {

    private ResourceHandleUtil() {

    }

    public static byte[] bytesFra(Resource vedlegg) {
        try (InputStream is = vedlegg.getInputStream()) {
            return copyToByteArray(is);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
