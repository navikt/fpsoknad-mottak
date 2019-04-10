package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static org.springframework.http.MediaType.parseMediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;

public final class MultipartMixedAwareMessageConverter extends FormHttpMessageConverter {

    private static final Logger LOG = LoggerFactory.getLogger(MultipartMixedAwareMessageConverter.class);

    public static final String MULTIPART_MIXED_VALUE = "multipart/mixed";
    public static final MediaType MULTIPART_MIXED = parseMediaType(MULTIPART_MIXED_VALUE);
    private Charset multipartCharset;
    private Charset charset = DEFAULT_CHARSET;

    private List<HttpMessageConverter<?>> partConverters = new ArrayList<>();

    public MultipartMixedAwareMessageConverter() {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        partConverters.add(new ByteArrayHttpMessageConverter());
        partConverters.add(stringHttpMessageConverter);
        partConverters.add(new ResourceHttpMessageConverter());
        applyDefaultCharset();
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return MultiValueMap.class.isAssignableFrom(clazz);
    }

    private boolean isFilenameCharsetSet() {
        return this.multipartCharset != null;
    }

    private void applyDefaultCharset() {
        safeStream(partConverters)
                .filter(s -> s instanceof AbstractHttpMessageConverter)
                .map(s -> AbstractHttpMessageConverter.class.cast(s))
                .filter(s -> s.getDefaultCharset() != null)
                .forEach(s -> s.setDefaultCharset(charset));
    }

    @Override
    public void write(MultiValueMap<String, ?> map, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException {
        writeMultipart((MultiValueMap<String, Object>) map, outputMessage);
    }

    private void writeMultipart(MultiValueMap<String, Object> parts, HttpOutputMessage outputMessage)
            throws IOException {

        byte[] boundary = generateMultipartBoundary();
        LOG.debug("Sender multipart ({} deler)", parts.size());

        Map<String, String> parameters = new HashMap<>(2);
        parameters.put("boundary", new String(boundary, "US-ASCII"));

        if (!isFilenameCharsetSet()) {
            parameters.put("charset", this.charset.name());
        }

        HttpHeaders headers = outputMessage.getHeaders();
        headers.setContentType(new MediaType(MULTIPART_MIXED, parameters));
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
            streamingOutputMessage.setBody(outputStream -> {
                writeParts(outputStream, parts, boundary);
                writeEnd(outputStream, boundary);
            });
        } else {
            writeParts(outputMessage.getBody(), parts, boundary);
            writeEnd(outputMessage.getBody(), boundary);
        }
    }

    private void writeParts(OutputStream os, MultiValueMap<String, Object> parts, byte[] boundary)
            throws IOException {
        LOG.debug("Sender {} deler", parts.size());
        for (Map.Entry<String, List<Object>> entry : parts.entrySet()) {
            String name = entry.getKey();
            for (Object part : entry.getValue()) {
                if (part != null) {
                    writeBoundary(os, boundary);
                    writePart(name, getHttpEntity(part), os);
                    writeNewLine(os);
                }
            }
        }
    }

    private void writePart(String name, HttpEntity<?> partEntity, OutputStream os) throws IOException {
        Object partBody = partEntity.getBody();
        if (partBody == null) {
            throw new IllegalStateException("Intet innhold for del '" + name + "': " + partEntity);
        }
        Class<?> partType = partBody.getClass();
        HttpHeaders partHeaders = partEntity.getHeaders();
        MediaType partContentType = partHeaders.getContentType();
        for (HttpMessageConverter<?> converter : partConverters) {
            if (converter.canWrite(partType, partContentType)) {
                Charset charset = isFilenameCharsetSet() ? StandardCharsets.US_ASCII : this.charset;
                HttpOutputMessage multipartMessage = new MultipartHttpOutputMessage(os, charset);
                multipartMessage.getHeaders().setContentDispositionFormData(name, getFilename(partBody));
                if (!partHeaders.isEmpty()) {
                    multipartMessage.getHeaders().putAll(partHeaders);
                }
                ((HttpMessageConverter<Object>) converter).write(partBody, partContentType, multipartMessage);
                return;
            }
        }
        throw new HttpMessageNotWritableException("Could not write request: no suitable HttpMessageConverter " +
                "found for request type [" + partType.getName() + "]");
    }

    public Charset getMultipartCharset() {
        return multipartCharset;
    }

    @Override
    public void setMultipartCharset(Charset multipartCharset) {
        this.multipartCharset = multipartCharset;
    }

    public Charset getCharset() {
        return charset;
    }

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    private static void writeBoundary(OutputStream os, byte[] boundary) throws IOException {
        os.write('-');
        os.write('-');
        os.write(boundary);
        writeNewLine(os);
    }

    private static void writeEnd(OutputStream os, byte[] boundary) throws IOException {
        os.write('-');
        os.write('-');
        os.write(boundary);
        os.write('-');
        os.write('-');
        writeNewLine(os);
    }

    private static void writeNewLine(OutputStream os) throws IOException {
        os.write('\r');
        os.write('\n');
    }

    private static class MultipartHttpOutputMessage implements HttpOutputMessage {

        private final OutputStream outputStream;

        private final Charset charset;

        private final HttpHeaders headers = new HttpHeaders();

        private boolean headersWritten = false;

        public MultipartHttpOutputMessage(OutputStream outputStream, Charset charset) {
            this.outputStream = outputStream;
            this.charset = charset;
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
        }

        @Override
        public OutputStream getBody() throws IOException {
            writeHeaders();
            return this.outputStream;
        }

        private void writeHeaders() throws IOException {
            if (!this.headersWritten) {
                for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                    byte[] headerName = getBytes(entry.getKey());
                    for (String headerValueString : entry.getValue()) {
                        this.outputStream.write(headerName);
                        this.outputStream.write(':');
                        this.outputStream.write(' ');
                        this.outputStream.write(getBytes(headerValueString));
                        writeNewLine(this.outputStream);
                    }
                }
                writeNewLine(this.outputStream);
                this.headersWritten = true;
            }
        }

        private byte[] getBytes(String name) {
            return name.getBytes(this.charset);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [multipartCharset=" + multipartCharset + ", charset=" + charset
                + ", partConverters=" + partConverters + "]";
    }

}