package no.nav.foreldrepenger.lookup.rest.sak;

// Format 2017-08-04T23:56:09+02:00 must be handled

public class RemoteSakMapper {

    private RemoteSakMapper() {

    }

    public static Sak map(RemoteSak remoteSak) {
        return new Sak(Integer.toString(remoteSak.getId()),
                remoteSak.getTema(),
                remoteSak.getApplikasjon(),
                remoteSak.getFagsakNr(),
                "",
                remoteSak.getOpprettetTidspunkt().toLocalDate(),
                remoteSak.getOpprettetAv());
    }

}
