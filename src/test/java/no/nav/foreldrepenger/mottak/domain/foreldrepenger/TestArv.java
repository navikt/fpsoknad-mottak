package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestArv {

    @Test
    public void testA() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new B(42)));
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new C(42)));
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new D(42)));

    }
}

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = B.class, name = "nameB"),
        @Type(value = C.class, name = "nameC"),
        @Type(value = D.class, name = "nameD"),
})
abstract class A {
    private final int i;

    public int getI() {
        return i;
    }

    public A(int i) {
        this.i = i;
    }

}

class B extends A {

    public B(int i) {
        super(i);
    }

}

class C extends A {

    public C(int i) {
        super(i);
    }

}

class D extends B {

    public D(int i) {
        super(i);
    }

}
