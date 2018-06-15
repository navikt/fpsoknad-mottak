package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import static java.time.LocalDate.now;

public class BarnMorRelasjonSjekkendeBarnutvelger implements Barnutvelger {

    private final int monthsBack;

    public BarnMorRelasjonSjekkendeBarnutvelger(int months) {
        this.monthsBack = months;
    }

    @Override
    public boolean erStonadsberettigetBarn(Fodselsnummer fnrMor, Barn barn) {
        return fnrMor.equals(barn.getFnrMor()) && barn.getFødselsdato().isAfter(now().minusMonths(monthsBack));
    }
}
