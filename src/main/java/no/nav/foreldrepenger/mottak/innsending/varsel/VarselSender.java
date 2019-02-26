package no.nav.foreldrepenger.mottak.innsending.varsel;

import org.springframework.stereotype.Service;

@Service
public class VarselSender {

    private final VarselConnection connection;
    private final VarselXMLGenerator generator;

    public VarselSender(VarselConnection connection, VarselXMLGenerator generator) {
        this.connection = connection;
        this.generator = generator;
    }

    public void varsle(Varsel varsel) {
        connection.varsle(generator.tilXml(varsel));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", generator=" + generator + "]";
    }
}
