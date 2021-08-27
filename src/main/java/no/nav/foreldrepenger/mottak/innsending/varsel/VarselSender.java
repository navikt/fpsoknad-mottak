package no.nav.foreldrepenger.mottak.innsending.varsel;

import org.springframework.stereotype.Service;

@Service
class VarselSender implements Varsler {

    private final VarselConnection connection;

    public VarselSender(VarselConnection connection) {
        this.connection = connection;
    }

    @Override
    public void varsle(Varsel varsel) {
        connection.varsle(varsel);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + "]";
    }
}
