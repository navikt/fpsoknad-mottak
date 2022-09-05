package no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto;

public record UtenlandskKontoInfo(String banknavn,
                                  String bankkode,
                                  String bankLandkode,
                                  String valutakode,
                                  String swiftBicKode,
                                  String bankadresse1,
                                  String bankadresse2,
                                  String bankadresse3) {
}
