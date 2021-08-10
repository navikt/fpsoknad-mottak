package no.nav.foreldrepenger.mottak.domain.serialization;

import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.arrayNode;
import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.textValue;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;

public class MedlemsskapDeserializer extends StdDeserializer<Medlemsskap> {

    public MedlemsskapDeserializer() {
        this(null);
    }

    public MedlemsskapDeserializer(Class<Medlemsskap> medlemsskap) {
        super(medlemsskap);
    }

    @Override
    public Medlemsskap deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        return new Medlemsskap(tidligereOpphold(rootNode, parser), framtidigOpphold(rootNode, parser));
    }

    private static TidligereOppholdsInformasjon tidligereOpphold(JsonNode rootNode, JsonParser parser) {
        return new TidligereOppholdsInformasjon(arbeidsInfo(rootNode),
                utenlandsOpphold(rootNode, parser, "utenlandsopphold"));
    }

    private static FramtidigOppholdsInformasjon framtidigOpphold(JsonNode rootNode, JsonParser parser) {
        return new FramtidigOppholdsInformasjon(utenlandsOpphold(rootNode, parser, "framtidigUtenlandsopphold"));
    }

    private static List<Utenlandsopphold> utenlandsOpphold(JsonNode rootNode, JsonParser parser, String nodeName) {
        return utenlandsOpphold(iterator(arrayNode(rootNode, nodeName)), parser);
    }

    private static List<Utenlandsopphold> utenlandsOpphold(Iterator<JsonNode> iterator, JsonParser parser) {
        return StreamSupport
                .stream(((Iterable<JsonNode>) () -> iterator).spliterator(), false)
                .map(s -> utenlandopphold(iterator, parser.getCodec()))
                .toList();
    }

    private static Utenlandsopphold utenlandopphold(Iterator<JsonNode> utland, ObjectCodec codec) {
        try {
            JsonParser parser = ObjectNode.class.cast(utland.next()).traverse();
            parser.setCodec(codec);
            return parser.readValueAs(Utenlandsopphold.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static ArbeidsInformasjon arbeidsInfo(JsonNode rootNode) {
        return ArbeidsInformasjon.valueOf(textValue(rootNode, "arbeidSiste12"));
    }

    private static Iterator<JsonNode> iterator(ArrayNode utland) {
        return utland != null ? utland.iterator() : Collections.emptyIterator();
    }
}
