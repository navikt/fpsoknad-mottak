package no.nav.foreldrepenger.mottak.innsending.fpfordel;

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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FPFordelConfiguration {
    private static final class MultipartMixedAwreFormMessageConverter extends FormHttpMessageConverter {

        private List<MediaType> supportedMediaTypes = new ArrayList<>();

        private List<HttpMessageConverter<?>> partConverters = new ArrayList<>();

        public MultipartMixedAwreFormMessageConverter() {
            this.supportedMediaTypes.add(MediaType.parseMediaType("multipart/mixed"));

            StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
            stringHttpMessageConverter.setWriteAcceptCharset(false); // see SPR-7316

            this.partConverters.add(new ByteArrayHttpMessageConverter());
            this.partConverters.add(stringHttpMessageConverter);
            this.partConverters.add(new ResourceHttpMessageConverter());
        }

        @Override
        public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
            LOG.info("Checking if we can write  {} for {}", clazz, mediaType);
            if (!MultiValueMap.class.isAssignableFrom(clazz)) {
                return false;
            }
            if (mediaType == null || MediaType.ALL.equals(mediaType)) {
                return true;
            }
            /*
             * for (MediaType supportedMediaType : getSupportedMediaTypes()) { if
             * (supportedMediaType.isCompatibleWith(mediaType)) { return true; } }
             */
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void write(MultiValueMap<String, ?> map, @Nullable MediaType contentType,
                HttpOutputMessage outputMessage)
                throws IOException, HttpMessageNotWritableException {
            writeMultipart((MultiValueMap<String, Object>) map, outputMessage);
        }

        private void writeMultipart(final MultiValueMap<String, Object> parts,
                HttpOutputMessage outputMessage) throws IOException {

            final byte[] boundary = generateMultipartBoundary();
            Map<String, String> parameters = new HashMap<>(2);
            parameters.put("boundary", new String(boundary, "US-ASCII"));
            /*
             * if (!isFilenameCharsetSet()) { parameters.put("charset",
             * this.charset.name()); }
             */

            MediaType contentType = new MediaType(MediaType.MULTIPART_FORM_DATA, parameters);
            HttpHeaders headers = outputMessage.getHeaders();
            headers.setContentType(contentType);

            if (outputMessage instanceof StreamingHttpOutputMessage) {
                StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
                streamingOutputMessage.setBody(outputStream -> {
                    writeParts(outputStream, parts, boundary);
                    writeEnd(outputStream, boundary);
                });
            }
            else {
                writeParts(outputMessage.getBody(), parts, boundary);
                writeEnd(outputMessage.getBody(), boundary);
            }
        }

        private void writeParts(OutputStream os, MultiValueMap<String, Object> parts, byte[] boundary)
                throws IOException {
            LOG.info("Writing {} parts", parts.size());
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
                throw new IllegalStateException("Empty body for part '" + name + "': " + partEntity);
            }
            Class<?> partType = partBody.getClass();
            HttpHeaders partHeaders = partEntity.getHeaders();
            MediaType partContentType = partHeaders.getContentType();
            LOG.info("Trying to write part {} of type {}", name, partContentType);
            for (HttpMessageConverter<?> messageConverter : partConverters) {
                if (messageConverter.canWrite(partType, partContentType)) {
                    /*
                     * Charset charset = isFilenameCharsetSet() ? StandardCharsets.US_ASCII :
                     * this.charset;
                     */
                    LOG.info("Writing part using {}", messageConverter.getClass().getSimpleName());
                    HttpOutputMessage multipartMessage = new MultipartHttpOutputMessage(os, StandardCharsets.US_ASCII);
                    multipartMessage.getHeaders().setContentDispositionFormData(name, getFilename(partBody));
                    if (!partHeaders.isEmpty()) {
                        multipartMessage.getHeaders().putAll(partHeaders);
                    }
                    ((HttpMessageConverter<Object>) messageConverter).write(partBody, partContentType,
                            multipartMessage);
                    return;
                }
            }
            throw new HttpMessageNotWritableException("Could not write request: no suitable HttpMessageConverter " +
                    "found for request type [" + partType.getName() + "]");
        }

        private void writeBoundary(OutputStream os, byte[] boundary) throws IOException {
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
                return (this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers);
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
                            byte[] headerValue = getBytes(headerValueString);
                            this.outputStream.write(headerName);
                            this.outputStream.write(':');
                            this.outputStream.write(' ');
                            this.outputStream.write(headerValue);
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

    }

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelConfiguration.class);

    @Bean
    public RestTemplate restTemplate(FPFordelConfig cfg, ClientHttpRequestInterceptor... interceptors) {

        RestTemplate template = new RestTemplateBuilder()
                .rootUri(cfg.getUri())
                .interceptors(interceptors)
                .build();
        LOG.info("Adding converter for multipart/mixed");
        template.getMessageConverters().add(new MultipartMixedAwreFormMessageConverter());
        return template;
    }

}
